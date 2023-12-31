package nl.tudelft.sem.yumyumnow.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import nl.tudelft.sem.yumyumnow.users.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Rating;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.services.TestDataInitializer;
import nl.tudelft.sem.yumyumnow.users.validators.CourierValidator;
import org.junit.jupiter.api.Test;
import nl.tudelft.sem.yumyumnow.users.repositories.CourierRepository;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.services.CourierService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourierServiceTest {

    private CourierRepository courierRepository = mock(CourierRepository.class);
    private UserRepository userRepository  = mock(UserRepository.class);
    private CourierValidator courierValidator = mock(CourierValidator.class);
    private CourierBuilder courierBuilder = mock(CourierBuilder.class);
    private CourierService courierService = new CourierService(courierRepository, userRepository, courierBuilder, courierValidator);

    @Test
    public void testGetCourierById_WhenCourierExists() throws UserNotFoundException {
        Long courierId = 1L;
        Courier expectedCourier = new Courier(); // Create a Courier instance for testing
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(expectedCourier));

        Courier result = courierService.getCourierById(courierId);

        assertEquals(expectedCourier, result);
    }

    @Test
    public void testGetCourierById_WhenCourierDoesNotExist() {
        Long courierId = 2L;
        when(courierRepository.findById(courierId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> courierService.getCourierById(courierId));
    }



    @Test
    public void testCreateCourierSuccess() throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException {
        // Arrange
        CourierRequestModel requestModel = TestDataInitializer.createValidCourierRequestModel();
        Courier dummyCourier = TestDataInitializer.createDummyCourier();
        when(courierBuilder.build(any(), any())).thenReturn(dummyCourier);
        when(courierRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(courierRepository.saveAndFlush(any())).thenReturn(dummyCourier);

        // Act
        Courier result = courierService.createCourier(requestModel);

        // Assert
        assertEquals(dummyCourier, result);
        verify(courierValidator).validate(requestModel);
        verify(courierBuilder).build(any(), any());
        verify(courierRepository).findByEmail(any());
        verify(courierRepository).saveAndFlush(any());
    }

    @Test
    public void testCreateCourierEmailAlreadyInUse() throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException {
        // Arrange
        CourierRequestModel requestModel = TestDataInitializer.createValidCourierRequestModel();
        when(courierRepository.findByEmail(any())).thenReturn(Optional.of(TestDataInitializer.createDummyCourier()));

        // Act and Assert
        assertThrows(EmailAlreadyInUseException.class, () -> courierService.createCourier(requestModel));
    }

    @Test
    public void testCreateCourierInvalidField() throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException {
        // Arrange
        CourierRequestModel requestModel = TestDataInitializer.createInvalidCourierRequestModel();
        doThrow(new InvalidFieldException("fieldName", "invalidValue")).when(courierValidator).validate(requestModel);

        // Act and Assert
        assertThrows(InvalidFieldException.class, () -> courierService.createCourier(requestModel));
    }

    private List<Courier> createMockCouriers() {
        List<Courier> mockCouriers = new ArrayList<>();

        Courier courier1 = new Courier();
        courier1.setId(1L);
        courier1.setEmail("courier1@gmail.com");
        courier1.setName("John");
        courier1.setSurname("Doe");
        mockCouriers.add(courier1);

        Courier courier2 = new Courier();
        courier2.setId(2L);
        courier2.setEmail("courier2@gmail.com");
        courier2.setName("Jane");
        courier2.setSurname("Smith");
        mockCouriers.add(courier2);

        return mockCouriers;
    }
    @Test
    public void testGetAllCouriersSuccess() {
        // Arrange
        List<Courier> mockCouriers = createMockCouriers();
        when(courierRepository.findAll()).thenReturn(mockCouriers);
        // Act
        List<Courier> result = courierService.getAllCouriers();

        // Assert
        assertEquals(mockCouriers, result);
    }

    @Test
    public void testGetAllCouriersEmptyList() {
        // Arrange
        when(courierRepository.findAll()).thenReturn(List.of());

        // Act
        List<Courier> result = courierService.getAllCouriers();

        // Assert
        assertEquals(0, result.size());
    }


    @Test
    public void testDeleteCourierEmpty() throws UserNotFoundException, NotAuthorizedException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Courier courier = new Courier();
        courier.setId(userId1);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotAuthorizedException.class, () -> courierService.deleteCourier(userId1, userId2));

    }

    @Test
    public void testDeleteCourierAuthorizedAdminSuccess() throws NotAuthorizedException, UserNotFoundException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Courier courier = new Courier();
        courier.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        // Act
        courierService.deleteCourier(userId1, userId2);

        // Assert
        verify(courierRepository).deleteById(userId1);
        verify(courierRepository).flush();
    }

    @Test
    public void testDeleteCourierAuthorizedUser() throws NotAuthorizedException, UserNotFoundException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 1L;

        Courier courier = new Courier();
        courier.setId(userId1);


        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));

        // Act
        courierService.deleteCourier(userId1, userId2);

        // Assert
        verify(courierRepository).deleteById(userId1);
        verify(courierRepository).flush();
    }

    @Test
    public void testDeleteCourierNotAuthorized() throws UserNotFoundException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Courier courier = new Courier();
        courier.setId(userId1);

        User nonAdminUser = new User();
        nonAdminUser.setId(userId2);
        nonAdminUser.setRole(User.RoleEnum.COURIER);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(nonAdminUser));

        // Act and Assert
        assertThrows(NotAuthorizedException.class, () -> courierService.deleteCourier(userId1, userId2));
    }

    @Test
    public void testDeleteCourierUserNotFound() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        when(courierRepository.findById(userId1)).thenReturn(Optional.empty());
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> courierService.deleteCourier(userId1, userId2));
    }

    @Test
    public void testUpdateCourierEmpty() throws UserNotFoundException, NotAuthorizedException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        CourierRequestModel courierRequestModel = new CourierRequestModel("newemail@gmail.com", "NewName", "NewSurname");

        Courier courierInitial = new Courier();
        courierInitial.setId(userId1);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courierInitial));
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());
        when(courierBuilder.build(any(), any())).thenReturn(courierInitial);

        assertThrows(NotAuthorizedException.class, () -> courierService.updateCourier(userId1, userId2, courierRequestModel));


    }

    @Test
    public void testUpdateCourierAuthorizedAdminSuccess() throws UserNotFoundException, NotAuthorizedException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        CourierRequestModel courierRequestModel = new CourierRequestModel("newemail@gmail.com", "NewName", "NewSurname");

        Courier courierInitial = new Courier();
        courierInitial.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courierInitial));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));
        when(courierBuilder.build(any(), any())).thenReturn(courierInitial);

        // Act
        courierService.updateCourier(userId1, userId2, courierRequestModel);

        // Assert
        verify(courierBuilder).build(courierInitial, courierRequestModel);
        verify(courierRepository).saveAndFlush(courierInitial);
    }

    @Test
    public void testUpdateCourierAuthorizedUser() throws UserNotFoundException, NotAuthorizedException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 1L;
        CourierRequestModel courierRequestModel = new CourierRequestModel("newemail@gmail.com", "NewName", "NewSurname");

        Courier courierInitial = new Courier();
        courierInitial.setId(userId1);


        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courierInitial));
        when(courierBuilder.build(any(), any())).thenReturn(courierInitial);

        // Act
        courierService.updateCourier(userId1, userId2, courierRequestModel);

        // Assert
        verify(courierBuilder).build(courierInitial, courierRequestModel);
        verify(courierRepository).saveAndFlush(courierInitial);
    }

    @Test
    public void testUpdateCourierNotAuthorized() throws UserNotFoundException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        CourierRequestModel courierRequestModel = new CourierRequestModel("newemail@gmail.com", "NewName", "NewSurname");

        Courier courierInitial = new Courier();
        courierInitial.setId(userId1);

        User nonAdminUser = new User();
        nonAdminUser.setId(userId2);
        nonAdminUser.setRole(User.RoleEnum.COURIER);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courierInitial));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(nonAdminUser));

        // Act and Assert
        assertThrows(NotAuthorizedException.class, () -> courierService.updateCourier(userId1, userId2, courierRequestModel));
    }

    @Test
    public void testUpdateCourierUserNotFound() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        CourierRequestModel courierRequestModel = new CourierRequestModel("newemail@gmail.com", "NewName", "NewSurname");

        when(courierRepository.findById(userId1)).thenReturn(Optional.empty());
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> courierService.updateCourier(userId1, userId2, courierRequestModel));
    }

    @Test
    public void testGetRatingsForCourierSuccess() throws UserNotFoundException {
        // Arrange
        Long userId = 1L;

        Rating rating1 = new Rating();
        rating1.setUserId(userId);
        rating1.setRating(5L);

        Rating rating2 = new Rating();
        rating2.setUserId(userId);
        rating2.setRating(4L);

        List<Rating> mockRatings = new ArrayList<>();
        mockRatings.add(rating1);
        mockRatings.add(rating2);

        Courier courier = new Courier();
        courier.setId(userId);
        courier.setRatings(mockRatings);

        when(courierRepository.findById(userId)).thenReturn(Optional.of(courier));

        // Act
        List<Rating> result = courierService.getRatingsForCourier(userId);

        // Assert
        assertEquals(mockRatings, result);
    }

    @Test
    public void testGetRatingsForCourierUserNotFound() {
        // Arrange
        Long userId = 1L;
        when(courierRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> courierService.getRatingsForCourier(userId));
    }

    @Test
    public void testUpdateRatingsForCourierEmpty() throws UserNotFoundException, NotAuthorizedException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Rating rating1 = new Rating();
        rating1.setUserId(userId1);
        rating1.setRating(5L);

        Rating rating2 = new Rating();
        rating2.setUserId(userId1);
        rating2.setRating(4L);

        List<Rating> ratings = new ArrayList<>();
        ratings.add(rating1);
        ratings.add(rating2);

        Courier courier = new Courier();
        courier.setId(userId1);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        assertThrows(NotAuthorizedException.class, () -> courierService.updateRatingsForCourier(userId1, userId2, ratings));

    }

    @Test
    public void testUpdateRatingsForCourierAuthorizedAdminSuccess() throws UserNotFoundException, NotAuthorizedException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Rating rating1 = new Rating();
        rating1.setUserId(userId1);
        rating1.setRating(5L);

        Rating rating2 = new Rating();
        rating2.setUserId(userId1);
        rating2.setRating(4L);

        List<Rating> ratings = new ArrayList<>();
        ratings.add(rating1);
        ratings.add(rating2);

        Courier courier = new Courier();
        courier.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        // Act
        courierService.updateRatingsForCourier(userId1, userId2, ratings);

        // Assert
        assertEquals(ratings, courier.getRatings());
        verify(courierRepository).saveAndFlush(courier);
    }

    @Test
    public void testUpdateRatingsForCourierNotAuthorized() throws UserNotFoundException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Rating rating1 = new Rating();
        rating1.setUserId(userId1);
        rating1.setRating(5L);

        Rating rating2 = new Rating();
        rating2.setUserId(userId1);
        rating2.setRating(4L);

        List<Rating> ratings = new ArrayList<>();
        ratings.add(rating1);
        ratings.add(rating2);

        Courier courier = new Courier();
        courier.setId(userId1);

        User nonAdminUser = new User();
        nonAdminUser.setId(userId2);
        nonAdminUser.setRole(User.RoleEnum.COURIER);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(nonAdminUser));

        // Act and Assert
        assertThrows(NotAuthorizedException.class, () -> courierService.updateRatingsForCourier(userId1, userId2, ratings));
    }

    @Test
    public void testUpdateRatingsForCourierUserNotFound() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Rating rating1 = new Rating();
        rating1.setUserId(userId1);
        rating1.setRating(5L);

        Rating rating2 = new Rating();
        rating2.setUserId(userId1);
        rating2.setRating(4L);

        List<Rating> ratings = new ArrayList<>();
        ratings.add(rating1);
        ratings.add(rating2);

        when(courierRepository.findById(userId1)).thenReturn(Optional.empty());
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> courierService.updateRatingsForCourier(userId1, userId2, ratings));
    }

    @Test
    public void testUpdateCourierActivationStatusSuccess() throws UserNotFoundException, NotAuthorizedException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean activationStatus = true;

        Courier courier = new Courier();
        courier.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        // Act
        courierService.updateCourierActivationStatus(userId1, userId2, activationStatus);

        // Assert
        assertEquals(activationStatus, courier.getActivated());
        verify(courierRepository).saveAndFlush(courier);
    }

    @Test
    public void testUpdateCourierActivationStatusNotAuthorized() throws UserNotFoundException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean activationStatus = true;

        Courier courier = new Courier();
        courier.setId(userId1);

        User nonAdminUser = new User();
        nonAdminUser.setId(userId2);
        nonAdminUser.setRole(User.RoleEnum.COURIER);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(nonAdminUser));

        // Act and Assert
        assertThrows(NotAuthorizedException.class, () -> courierService.updateCourierActivationStatus(userId1, userId2, activationStatus));
    }

    @Test
    public void testUpdateCourierActivationStatusUserNotFound() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean activationStatus = true;

        when(courierRepository.findById(userId1)).thenReturn(Optional.empty());
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> courierService.updateCourierActivationStatus(userId1, userId2, activationStatus));
    }

    @Test
    public void testUpdateCourierBlockStatusSuccess() throws UserNotFoundException, NotAuthorizedException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean blockStatus = true;

        Courier courier = new Courier();
        courier.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        // Act
        courierService.updateCourierBlockStatus(userId1, userId2, blockStatus);

        // Assert
        assertEquals(blockStatus, courier.getBlocked());
        verify(courierRepository).saveAndFlush(courier);
    }

    @Test
    public void testUpdateCourierBlockStatusNotAuthorized() throws UserNotFoundException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean blockStatus = true;

        Courier courier = new Courier();
        courier.setId(userId1);

        User nonAdminUser = new User();
        nonAdminUser.setId(userId2);
        nonAdminUser.setRole(User.RoleEnum.COURIER);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(nonAdminUser));

        // Act and Assert
        assertThrows(NotAuthorizedException.class, () -> courierService.updateCourierBlockStatus(userId1, userId2, blockStatus));
    }

    @Test
    public void testUpdateCourierBlockStatusUserNotFound() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean blockStatus = true;

        when(courierRepository.findById(userId1)).thenReturn(Optional.empty());
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> courierService.updateCourierBlockStatus(userId1, userId2, blockStatus));
    }
}