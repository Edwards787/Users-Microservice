package nl.tudelft.sem.yumyumnow.users.controllers;


import nl.tudelft.sem.yumyumnow.users.controllers.CourierController;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Rating;
import nl.tudelft.sem.yumyumnow.users.services.CourierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CourierControllerTest {
    private CourierService courierService;
    private CourierController courierController;

    @BeforeEach
    public void setUp(){
        courierService = mock(CourierService.class);
        courierController = new CourierController(courierService);
    }

    @Test
    public void testGetCourierByIdSuccess() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;
        Courier mockCourier = new Courier();
        when(courierService.getCourierById(userId)).thenReturn(mockCourier);

        // Act
        ResponseEntity<Courier> result = courierController.getCourierById(userId);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockCourier, result.getBody());
    }

    @Test
    public void testGetCourierByIdUserNotFound() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;
        when(courierService.getCourierById(userId)).thenThrow(new UserNotFoundException());

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> courierController.getCourierById(userId));
    }

    @Test
    public void testCreateCourierSuccess()
        throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {
        // Arrange
        CourierRequestModel courierRequestModel = new CourierRequestModel("test@example.com", "John", "Doe");
        Courier mockCourier = new Courier();
        when(courierService.createCourier(courierRequestModel)).thenReturn(mockCourier);

        // Act
        ResponseEntity<Courier> result = courierController.createCourier(courierRequestModel);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockCourier, result.getBody());
    }

    @Test
    public void testCreateCourierMissingBody()
        throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {
        // Arrange
        CourierRequestModel courierRequestModel = new CourierRequestModel(null, "John", "Doe");
        when(courierService.createCourier(courierRequestModel)).thenThrow(new MissingBodyException());

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> courierController.createCourier(courierRequestModel));
    }

    @Test
    public void testCreateCourierInvalidField()
        throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {
        // Arrange
        CourierRequestModel courierRequestModel = new CourierRequestModel("test@example.com", null, "Doe");
        when(courierService.createCourier(courierRequestModel)).thenThrow(new InvalidFieldException("name", null));

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> courierController.createCourier(courierRequestModel));
    }

    // Add similar tests for getAllCouriers, deleteCourier, and updateCourier

    @Test
    public void testGetPastOrderIdsForCourierSuccess() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;
        List<Long> mockPastOrders = new ArrayList<>();
        mockPastOrders.add(100L);
        mockPastOrders.add(101L);
        when(courierService.getPastOrderIdsForCourier(userId)).thenReturn(mockPastOrders);

        // Act
        ResponseEntity<List<Long>> result = courierController.getPastOrderIdsForCourier(userId);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockPastOrders, result.getBody());
    }

    @Test
    public void testGetPastOrderIdsForCourierUserNotFound() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;
        when(courierService.getPastOrderIdsForCourier(userId)).thenThrow(new UserNotFoundException());

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> courierController.getPastOrderIdsForCourier(userId));
    }

    @Test
    public void testGetRatingsForCourierSuccess() throws UserNotFoundException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Rating rating1 = new Rating();
        rating1.setUserId(userId1);
        rating1.setRating(5L);

        Rating rating2 = new Rating();
        rating2.setUserId(userId1);
        rating2.setRating(4L);

        List<Rating> mockRatings = new ArrayList<>();
        mockRatings.add(rating1);
        mockRatings.add(rating2);
        when(courierService.getRatingsForCourier(userId1)).thenReturn(mockRatings);

        // Act
        ResponseEntity<List<Rating>> result = courierController.getRatingsForCourier(userId1);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockRatings, result.getBody());
    }

    @Test
    public void testGetRatingsForCourierUserNotFound() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;
        when(courierService.getRatingsForCourier(userId)).thenThrow(new UserNotFoundException());

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> courierController.getRatingsForCourier(userId));
    }

    @Test
    public void testUpdateRatingsForCourierSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Rating rating1 = new Rating();
        rating1.setUserId(userId1);
        rating1.setRating(5L);

        Rating rating2 = new Rating();
        rating2.setUserId(userId1);
        rating2.setRating(4L);

        List<Rating> mockRatings = new ArrayList<>();
        mockRatings.add(rating1);
        mockRatings.add(rating2);

        // Act
        ResponseEntity<Void> result = courierController.updateRatingsForCourier(userId1, userId2, mockRatings);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(courierService).updateRatingsForCourier(userId1, userId2, mockRatings);
    }

    @Test
    public void testUpdateCourierActivationStatusSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean activationStatus = true;

        // Act
        ResponseEntity<Void> result = courierController.updateCourierActivationStatus(userId1, userId2, activationStatus);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(courierService).updateCourierActivationStatus(userId1, userId2, activationStatus);
    }

    @Test
    public void testUpdateCourierActivationStatusUserNotFound()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean activationStatus = true;
        doThrow(new UserNotFoundException()).when(courierService).updateCourierActivationStatus(userId1, userId2, activationStatus);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> courierController.updateCourierActivationStatus(userId1, userId2, activationStatus));
    }

    @Test
    public void testUpdateCourierActivationStatusNotAuthorized()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean activationStatus = true;
        doThrow(new NotAuthorizedException()).when(courierService).updateCourierActivationStatus(userId1, userId2, activationStatus);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> courierController.updateCourierActivationStatus(userId1, userId2, activationStatus));
    }

    @Test
    public void testUpdateCourierBlockStatusSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean blockStatus = true;

        // Act
        ResponseEntity<Void> result = courierController.updateCourierBlockStatus(userId1, userId2, blockStatus);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(courierService).updateCourierBlockStatus(userId1, userId2, blockStatus);
    }

    @Test
    public void testUpdateCourierBlockStatusUserNotFound()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean blockStatus = true;
        doThrow(new UserNotFoundException()).when(courierService).updateCourierBlockStatus(userId1, userId2, blockStatus);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> courierController.updateCourierBlockStatus(userId1, userId2, blockStatus));
    }

    @Test
    public void testUpdateCourierBlockStatusNotAuthorized()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean blockStatus = true;
        doThrow(new NotAuthorizedException()).when(courierService).updateCourierBlockStatus(userId1, userId2, blockStatus);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> courierController.updateCourierBlockStatus(userId1, userId2, blockStatus));
    }
}
