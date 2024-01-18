package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.FeedbackApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Feedback;
import nl.tudelft.sem.yumyumnow.users.model.FeedbackRequestModel;
import nl.tudelft.sem.yumyumnow.users.services.FeedbackService;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidatorConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
public class FeedbackController implements FeedbackApi {
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService){
        this.feedbackService = feedbackService;
    }
    @Override
    public ResponseEntity<Feedback> createFeedback(FeedbackRequestModel feedbackRequestModel)
        throws MissingBodyException, InvalidFieldException {
        try {
            return ResponseEntity.ok(feedbackService.createFeedback(feedbackRequestModel));
        } catch (InvalidFieldException | MissingBodyException e) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        }
    }

    @Override
    public ResponseEntity<Void> deleteFeedback(Long feedbackId, Long userId) {
        try {
            feedbackService.deleteFeedback(feedbackId, userId);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NotAuthorizedException e) {
            throw new RuntimeException(e);
        } catch (MissingBodyException e) {
            throw new RuntimeException(e);
        } catch (FeedbackNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvalidFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        try {
            return ResponseEntity.ok(feedbackService.getAllFeedbacks());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Feedback> getFeedbackById(Long feedbackId)
        throws FeedbackNotFoundException {
        try {
            Feedback feedback = feedbackService.getFeedbackById(feedbackId);
            return ResponseEntity.ok(feedback);
        } catch (FeedbackNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
