package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import nl.tudelft.sem.yumyumnow.users.services.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FeedBackControllerTest {

    transient FeedbackService feedbackService;
    transient FeedbackController feedbackController;
    transient Feedback feedback1;
    transient Feedback feedback2;
    transient FeedbackRequestModel feedbackRequestModel;
    transient FeedbackRequestModel feedbackRequestModel2;


    @BeforeEach
    public void setUp(){
        feedbackService = mock(FeedbackService.class);
        feedbackController = new FeedbackController(feedbackService);
    }

    @BeforeEach
    public void setUpEntities(){
        feedback1 = new Feedback();
        feedback1.setId(1L);
        feedback1.setComment("comment");
        feedback1.setComplaint(true);
        feedback1.setStars(5L);

        feedback2 = new Feedback();
        feedback2.setId(2L);
        feedback2.setComment("comment");
        feedback2.setComplaint(false);
        feedback2.setStars(1L);

        feedbackRequestModel= new FeedbackRequestModel(3L,"comment", false);
        feedbackRequestModel2= new FeedbackRequestModel(null,"comment", false);
    }

    @Test
    void testUserCreateSuccess() throws MissingBodyException, InvalidFieldException, UserNotFoundException {
        when(feedbackService.createFeedback(feedbackRequestModel)).thenReturn(feedback1);
        assertThat(feedbackController.createFeedback(feedbackRequestModel)).isEqualTo(ResponseEntity.ok(feedback1));
    }

    @Test
    public void testCreateFeedbackMissingBody()
            throws MissingBodyException, InvalidFieldException, UserNotFoundException {
        when(feedbackService.createFeedback(null)).thenThrow(new MissingBodyException());
        assertThrows(ResponseStatusException.class, () -> {
            feedbackController.createFeedback(null);
        });
        try{
            feedbackController.createFeedback(null);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getReason()).isEqualTo("Body is missing");
        }
    }

    @Test
    public void testCreateFeedbackUserNotFound()
            throws MissingBodyException, InvalidFieldException, UserNotFoundException {
        when(feedbackService.createFeedback(feedbackRequestModel2)).thenThrow(new UserNotFoundException());
        assertThrows(ResponseStatusException.class, () -> {
            feedbackController.createFeedback(feedbackRequestModel2);
        });
        try{
            feedbackController.createFeedback(feedbackRequestModel2);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    public void testDeleteFeedbackValid()
            throws UserNotFoundException, NotAuthorizedException,
            MissingBodyException, InvalidFieldException, FeedbackNotFoundException {
        doNothing().when(feedbackService).deleteFeedback(1L, 2L);
        assertThat(feedbackController.deleteFeedback(1L, 2L)).isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        verify(feedbackService).deleteFeedback(1L, 2L);
    }

    @Test
    public void testDeleteFeedbackUnauthorized()
            throws UserNotFoundException, NotAuthorizedException,
            MissingBodyException, InvalidFieldException, FeedbackNotFoundException {
        doThrow(new NotAuthorizedException()).when(feedbackService).deleteFeedback(2L, 0L);
        assertThrows(ResponseStatusException.class, () -> {
            feedbackController.deleteFeedback(2L, 0L);
        });
        try{
            feedbackController.deleteFeedback(2L, 0L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus().value()).isEqualTo(403);
            assertThat(e.getReason()).isEqualTo("Not authorized");
        }
    }

    @Test
    public void testDeleteFeedbackNotFound()
            throws UserNotFoundException, NotAuthorizedException,
            MissingBodyException, InvalidFieldException, FeedbackNotFoundException {
        doThrow(new UserNotFoundException()).when(feedbackService).deleteFeedback(3L, 0L);
        assertThrows(ResponseStatusException.class, () -> {
            feedbackController.deleteFeedback(3L, 0L);
        });
        try{
            feedbackController.deleteFeedback(3L, 0L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus().value()).isEqualTo(404);
            assertThat(e.getReason()).isEqualTo("User not found");
        }
    }

    @Test
    public void testDeleteFeedbackMissingBody()
            throws UserNotFoundException, NotAuthorizedException,
            MissingBodyException, InvalidFieldException, FeedbackNotFoundException {
        doThrow(new MissingBodyException()).when(feedbackService).deleteFeedback(null, 0L);
        assertThrows(ResponseStatusException.class, () -> {
            feedbackController.deleteFeedback(null, 0L);
        });
        try{
            feedbackController.deleteFeedback(null, 0L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getReason()).isEqualTo("Body is missing");
        }
    }

    @Test
    public void testDeleteFeedbackFNotFound()
            throws UserNotFoundException, NotAuthorizedException,
            MissingBodyException, InvalidFieldException, FeedbackNotFoundException {
        doThrow(new FeedbackNotFoundException(10L)).when(feedbackService).deleteFeedback(10L, 0L);
        assertThrows(ResponseStatusException.class, () -> {
            feedbackController.deleteFeedback(10L, 0L);
        });
        try{
            feedbackController.deleteFeedback(10L, 0L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus().value()).isEqualTo(404);
            assertThat(e.getReason()).isEqualTo("Feedback with id 10 not found!");
        }
    }

    @Test
    public void testGetFeedbackById() throws  FeedbackNotFoundException {
        when(feedbackService.getFeedbackById(2L)).thenReturn(feedback2);
        assertThat(feedbackController.getFeedbackById(2L)).isEqualTo(ResponseEntity.ok(feedback2));
    }

    @Test
    public void testGetFeedbackByIdNotFound() throws FeedbackNotFoundException {
        when(feedbackService.getFeedbackById(100L)).thenThrow(new FeedbackNotFoundException(100L));
        assertThrows(ResponseStatusException.class, () -> {
            feedbackController.getFeedbackById(100L);
        });
        try{
            feedbackController.getFeedbackById(100L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getReason()).isEqualTo("Feedback with id 100 not found!");
        }
    }

    @Test
    void getAllFeedbackSuccess() {
        when(feedbackService.getAllFeedbacks()).thenReturn(List.of(feedback1,feedback2));
        ResponseEntity<List<Feedback>> res = feedbackController.getAllFeedbacks();
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isEqualTo(List.of(feedback1,feedback2));
    }

}
