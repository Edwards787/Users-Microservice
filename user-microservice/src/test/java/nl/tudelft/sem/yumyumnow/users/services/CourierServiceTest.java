package nl.tudelft.sem.yumyumnow.users.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import nl.tudelft.sem.yumyumnow.users.factories.CourierFactory;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import nl.tudelft.sem.yumyumnow.users.repositories.CourierRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourierServiceTest {

    transient CourierRepository courierRepository = mock(CourierRepository.class);
    transient UserRepository userRepository  = mock(UserRepository.class);
    transient CourierFactory courierBuilder = mock(CourierFactory.class);
    transient HttpService httpService = mock(HttpService.class);
    transient EmailService emailService = mock(EmailService.class);
    transient CourierService courierService = new CourierService(courierRepository, userRepository,
            emailService, courierBuilder, httpService);

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
    public void testCreateCourierSuccess()
        throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {
        // Arrange
        CourierRequestModel requestModel = TestDataInitializer.createValidCourierRequestModel();
        Courier dummyCourier = TestDataInitializer.createDummyCourier();
        when(courierBuilder.build(any())).thenReturn(dummyCourier);
        when(courierRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(courierRepository.saveAndFlush(any())).thenReturn(dummyCourier);

        // Act
        Courier result = courierService.createCourier(requestModel);

        // Assert
        assertEquals(dummyCourier, result);
        verify(courierBuilder).build(any());
        verify(courierRepository).findByEmail(any());
        verify(courierRepository).saveAndFlush(any());
        verify(emailService).sendEmail(argThat(x -> x.getRecipient().equals(requestModel.getEmail())));
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        // Assert
        assertThrows(UserNotFoundException.class, () -> courierService.deleteCourier(userId1, userId2));
    }

    @Test
    public void testDeleteCourierAuthorizedAdminSuccess()
        throws NotAuthorizedException, UserNotFoundException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Courier courier = new Courier();
        courier.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        // Act
        courierService.deleteCourier(userId1, userId2);

        // Assert
        verify(courierRepository).deleteById(userId1);
        verify(courierRepository).flush();
    }

    @Test
    public void testDeleteCourierAuthorizedUser()
        throws NotAuthorizedException, UserNotFoundException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 1L;

        Courier courier = new Courier();
        courier.setId(userId1);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
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

        assertThrows(UserNotFoundException.class, () -> courierService.updateCourier(userId1, userId2, courierRequestModel));
    }

    @Test
    public void testUpdateCourierAuthorizedAdminSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        CourierRequestModel courierRequestModel = new CourierRequestModel("newemail@gmail.com", "NewName", "NewSurname");
        courierRequestModel.setIbanNumber("");

        Courier courierInitial = new Courier();
        courierInitial.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courierInitial));
        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courierInitial));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        // Act
        courierService.updateCourier(userId1, userId2, courierRequestModel);

        // Assert
        verify(courierRepository).saveAndFlush(courierInitial);
    }

    @Test
    public void testUpdateCourierAuthorizedUser()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 1L;
        CourierRequestModel courierRequestModel = new CourierRequestModel("newemail@gmail.com", "NewName", "NewSurname");
        courierRequestModel.setIbanNumber("valid");

        Courier courierInitial = new Courier();
        courierInitial.setId(userId1);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courierInitial));
        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courierInitial));

        // Act
        courierService.updateCourier(userId1, userId2, courierRequestModel);

        // Assert
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courierInitial));
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(courierRepository.findById(userId1)).thenReturn(Optional.of(courier));
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> courierService.updateRatingsForCourier(userId1, userId2, ratings));
    }

    @Test
    public void testUpdateRatingsForCourierAuthorizedAdminSuccess()
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

        List<Rating> ratings = new ArrayList<>();
        ratings.add(rating1);
        ratings.add(rating2);

        Courier courier = new Courier();
        courier.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
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
    public void testUpdateCourierActivationStatusSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean activationStatus = true;

        Courier courier = new Courier();
        courier.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
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
    public void testUpdateCourierBlockStatusSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean blockStatus = true;

        Courier courier = new Courier();
        courier.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(courier));
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

    @Test
    public void testGetPastOrderFromCourierId() throws UserNotFoundException {
        Long userId1 = 1L;
        List<Long> pastOrder = List.of(1L, 2L, 3L);

        when(courierRepository.findById(userId1)).thenReturn(Optional.of(new Courier()));
        when(httpService.getPastOrderIdsForCourier(anyString(), anyLong())).thenReturn(pastOrder);

        List<Long> receivedValue = courierService.getPastOrderIdsForCourier(userId1);
        assertEquals(receivedValue, pastOrder);
    }

    @Test
    public void testGetPastOrderFromCourierIdNoUser() throws UserNotFoundException {
        Long userId1 = 1L;
        List<Long> pastOrder = List.of(1L, 2L, 3L);

        when(courierRepository.findById(userId1)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> courierService.getPastOrderIdsForCourier(userId1));
    }

    @Test
    public void testRateCourierSuccess1() throws UserNotFoundException, InvalidFieldException, MissingBodyException {
        // Arrange
        Long courierId = 3L;
        Long issuerId = 4L;
        Rating rating = new Rating();
        rating.setRating(3L);
        rating.setUserId(issuerId);

        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setRatings(new ArrayList<>());
        courier.setRole(User.RoleEnum.COURIER);

        Customer issuer = new Customer();
        issuer.setId(issuerId);
        issuer.setRole(User.RoleEnum.CUSTOMER);

        when(userRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(userRepository.findById(issuerId)).thenReturn(Optional.of(issuer));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        // Act
        courierService.rateCourier(courierId, rating);

        // Assert
        assertEquals(1, courier.getRatings().size());
        assertEquals(rating, courier.getRatings().get(0));
    }

    @Test
    public void testRateCourierSuccess2() throws UserNotFoundException, InvalidFieldException, MissingBodyException {
        // Arrange
        Long courierId = 3L;
        Long issuerId = 4L;
        Rating rating = new Rating();
        rating.setRating(1L);
        rating.setUserId(issuerId);

        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setRatings(new ArrayList<>());
        courier.setRole(User.RoleEnum.COURIER);

        Customer issuer = new Customer();
        issuer.setId(issuerId);
        issuer.setRole(User.RoleEnum.CUSTOMER);

        when(userRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(userRepository.findById(issuerId)).thenReturn(Optional.of(issuer));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        // Act
        courierService.rateCourier(courierId, rating);

        // Assert
        assertEquals(1, courier.getRatings().size());
        assertEquals(rating, courier.getRatings().get(0));
    }

    @Test
    public void testRateCourierSuccess3() throws UserNotFoundException, InvalidFieldException, MissingBodyException {
        // Arrange
        Long courierId = 3L;
        Long issuerId = 4L;
        Rating rating = new Rating();
        rating.setRating(5L);
        rating.setUserId(issuerId);

        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setRatings(new ArrayList<>());
        courier.setRole(User.RoleEnum.COURIER);

        Customer issuer = new Customer();
        issuer.setId(issuerId);
        issuer.setRole(User.RoleEnum.CUSTOMER);

        when(userRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(userRepository.findById(issuerId)).thenReturn(Optional.of(issuer));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        // Act
        courierService.rateCourier(courierId, rating);

        // Assert
        assertEquals(1, courier.getRatings().size());
        assertEquals(rating, courier.getRatings().get(0));
    }

    @Test
    public void testRateCourierIssuerNotFound() {
        // Arrange
        Long courierId = 3L;
        Long issuerId = 4L;
        Rating rating = new Rating();
        rating.setRating(3L);
        rating.setUserId(issuerId);

        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setRatings(new ArrayList<>());
        courier.setRole(User.RoleEnum.COURIER);

        when(userRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        // Act and assert
        assertThrows(UserNotFoundException.class, () -> courierService.rateCourier(courierId, rating));
    }

    @Test
    public void testRateCourierIssuerNonCustomer() {
        // Arrange
        Long courierId = 3L;
        Long issuerId = 4L;
        Rating rating = new Rating();
        rating.setRating(3L);
        rating.setUserId(issuerId);

        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setRatings(new ArrayList<>());
        courier.setRole(User.RoleEnum.COURIER);

        Vendor issuer = new Vendor();
        issuer.setId(issuerId);
        issuer.setRole(User.RoleEnum.VENDOR);

        when(userRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(userRepository.findById(issuerId)).thenReturn(Optional.of(issuer));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        // Act and assert
        assertThrows(UserNotFoundException.class, () -> courierService.rateCourier(courierId, rating));
    }

    @Test
    public void testRateCourierNotFound() {
        // Arrange
        Long courierId = 3L;
        Long issuerId = 4L;
        Rating rating = new Rating();
        rating.setRating(3L);
        rating.setUserId(issuerId);

        Customer issuer = new Customer();
        issuer.setId(issuerId);
        issuer.setRole(User.RoleEnum.CUSTOMER);

        when(userRepository.findById(issuerId)).thenReturn(Optional.of(issuer));

        // Act and assert
        assertThrows(UserNotFoundException.class, () -> courierService.rateCourier(courierId, rating));
    }

    @Test
    public void testRateCourierRatingInvalid1() {
        // Arrange
        Long courierId = 3L;
        Long issuerId = 4L;
        Rating rating = new Rating();
        rating.setRating(0L);
        rating.setUserId(issuerId);

        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setRatings(new ArrayList<>());
        courier.setRole(User.RoleEnum.COURIER);

        Customer issuer = new Customer();
        issuer.setId(issuerId);
        issuer.setRole(User.RoleEnum.CUSTOMER);

        when(userRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(userRepository.findById(issuerId)).thenReturn(Optional.of(issuer));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        // Act and assert
        assertThrows(InvalidFieldException.class, () -> courierService.rateCourier(courierId, rating));
    }

    @Test
    public void testRateCourierRatingInvalid2() {
        // Arrange
        Long courierId = 3L;
        Long issuerId = 4L;
        Rating rating = new Rating();
        rating.setRating(6L);
        rating.setUserId(issuerId);

        Courier courier = new Courier();
        courier.setId(courierId);
        courier.setRatings(new ArrayList<>());
        courier.setRole(User.RoleEnum.COURIER);

        Customer issuer = new Customer();
        issuer.setId(issuerId);
        issuer.setRole(User.RoleEnum.CUSTOMER);

        when(userRepository.findById(courierId)).thenReturn(Optional.of(courier));
        when(userRepository.findById(issuerId)).thenReturn(Optional.of(issuer));
        when(courierRepository.findById(courierId)).thenReturn(Optional.of(courier));

        // Act and assert
        assertThrows(InvalidFieldException.class, () -> courierService.rateCourier(courierId, rating));
    }

    @Test
    public void testRateCourierMissingBody() {
        // Arrange
        Long courierId = 3L;

        // Act and assert
        assertThrows(MissingBodyException.class, () -> courierService.rateCourier(courierId, null));
    }

}