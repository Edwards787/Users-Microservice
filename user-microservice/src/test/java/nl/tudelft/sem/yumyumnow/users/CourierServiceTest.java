package nl.tudelft.sem.yumyumnow.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import nl.tudelft.sem.yumyumnow.users.repositories.CourierRepository;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.services.CourierService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CourierServiceTest {

    private CourierRepository courierRepository = mock(CourierRepository.class);
    private UserRepository userRepository  = mock(UserRepository.class);
    private CourierService courierService = new CourierService(courierRepository, userRepository);

    @Test
    public void testCreateCourier_Success() {

        CourierRequestModel requestModel = new CourierRequestModel("joebiden@gmail.com", "joe", "biden");
        requestModel.setIbanNumber("TEST12345");

        when(courierRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(courierRepository.saveAndFlush(any(Courier.class))).thenReturn(new Courier());

        ResponseEntity<Courier> response = courierService.createCourier(requestModel);

        verify(courierRepository, times(1)).findByEmail(anyString());
        verify(courierRepository, times(1)).saveAndFlush(any(Courier.class));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateCourier_DuplicateEmail() {
        // Prepare test data
        CourierRequestModel requestModel = new CourierRequestModel("joebiden@gmail.com", "joe", "biden");
        requestModel.setIbanNumber("TEST12345");

        when(courierRepository.findByEmail(anyString())).thenReturn(Optional.of(new Courier()));

        assertThrows(StatusException.class, () -> courierService.createCourier(requestModel));

        verify(courierRepository, times(1)).findByEmail(anyString());
        verify(courierRepository, never()).saveAndFlush(any(Courier.class));
    }

    @Test
    public void testCreateCourier_EmptyEmail() {
        CourierRequestModel requestModel = new CourierRequestModel("", "John", "Doe");
        requestModel.setIbanNumber("TEST12345");


        StatusException exception = assertThrows(StatusException.class, () -> courierService.createCourier(requestModel));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testCreateCourier_EmptyName() {
        CourierRequestModel requestModel = new CourierRequestModel("john.doe@example.com", "", "Doe");
        requestModel.setIbanNumber("TEST12345");

        StatusException exception = assertThrows(StatusException.class, () -> courierService.createCourier(requestModel));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testCreateCourier_EmptySurname() {
        CourierRequestModel requestModel = new CourierRequestModel("john.doe@example.com", "John", "");
        requestModel.setIbanNumber("TEST12345");

        StatusException exception = assertThrows(StatusException.class, () -> courierService.createCourier(requestModel));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testCreateCourier_EmptyIban() {
        CourierRequestModel requestModel = new CourierRequestModel("john.doe@example.com", "John", "Doe");
        requestModel.setIbanNumber("");

        StatusException exception = assertThrows(StatusException.class, () -> courierService.createCourier(requestModel));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    public void testGetAllCouriers() {

        Courier courier1 = new Courier();
        courier1.setId(1L);
        courier1.setName("John");
        courier1.setSurname("Doe");
        courier1.setEmail("john.doe@example.com");

        Courier courier2 = new Courier();
        courier2.setId(2L);
        courier2.setName("Jane");
        courier2.setSurname("Smith");
        courier2.setEmail("jane.smith@example.com");

        List<Courier> mockCouriers = Arrays.asList(courier1, courier2);

        when(courierRepository.findAll()).thenReturn(mockCouriers);

        ResponseEntity<List<Courier>> response = courierService.getAllCouriers();

        verify(courierRepository, times(1)).findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCouriers, response.getBody());
    }

    @Test
    public void testGetCourierById_ExistingCourier() {

        Long courierId = 1L;
        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);
        mockCourier.setName("John");
        mockCourier.setSurname("Doe");
        mockCourier.setEmail("john.doe@example.com");


        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));


        ResponseEntity<Courier> response = courierService.getCourierById(courierId);


        verify(courierRepository, times(1)).findById(courierId);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCourier, response.getBody());
    }

    @Test
    public void testDeleteCourier_Success_UserIsOwner() {

        Long courierId = 1L;
        Long userId = 1L;

        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);

        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        ResponseEntity<Void> response = courierService.deleteCourier(courierId, userId);

        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, times(1)).deleteById(courierId);
        verify(courierRepository, times(1)).flush();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteCourier_Success_AdminAuthorized() {
        Long courierId = 1L;
        Long userId = 2L;

        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.ADMIN); // Set the role to ADMIN

        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));

        ResponseEntity<Void> response = courierService.deleteCourier(courierId, userId);

        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, times(1)).deleteById(courierId);
        verify(courierRepository, times(1)).flush();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeleteCourier_NotFound() {
        Long courierId = 1L;
        Long userId = 1L;

        when(courierRepository.findById(courierId)).thenReturn(Optional.empty());

        StatusException exception = assertThrows(StatusException.class,
                () -> courierService.deleteCourier(courierId, userId));

        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, never()).deleteById(anyLong());
        verify(courierRepository, never()).flush();

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    public void testDeleteCourier_Unauthorized() {
        Long courierId = 1L;
        Long userId = 3L;

        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);

        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        StatusException exception = assertThrows(StatusException.class,
                () -> courierService.deleteCourier(courierId, userId));

        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, never()).deleteById(anyLong());
        verify(courierRepository, never()).flush();

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
    }

    @Test
    public void testUpdateCourier_Success_UserIsOwner() {

        Long courierId = 1L;
        Long userId = 1L;

        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);

        Courier courierUpdate = new Courier();
        courierUpdate.setName("UpdatedName");


        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));


        ResponseEntity<Void> response = courierService.updateCourier(courierId, userId, courierUpdate);


        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, times(1)).save(mockCourier);
        verify(courierRepository, times(1)).flush();


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        assertEquals("UpdatedName", mockCourier.getName());
    }

    @Test
    public void testUpdateCourier_Success_AdminAuthorized() {


        Long courierId = 1L;
        Long userId = 2L;

        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);

        Courier courierUpdate = new Courier();
        courierUpdate.setName("UpdatedName");

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.ADMIN);


        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));


        ResponseEntity<Void> response = courierService.updateCourier(courierId, userId, courierUpdate);



        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, times(1)).save(mockCourier);
        verify(courierRepository, times(1)).flush();


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        assertEquals("UpdatedName", mockCourier.getName());
    }

    @Test
    public void testUpdateCourier_NotFound() {

        Long courierId = 1L;
        Long userId = 1L;

        Courier courierUpdate = new Courier();
        courierUpdate.setName("UpdatedName");


        when(courierRepository.findById(courierId)).thenReturn(Optional.empty());

        StatusException exception = assertThrows(StatusException.class,
                () -> courierService.updateCourier(courierId, userId, courierUpdate));


        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, never()).save(any(Courier.class));
        verify(courierRepository, never()).flush();


        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

    }

    @Test
    public void testUpdateCourier_Unauthorized() {

        Long courierId = 1L;
        Long userId = 3L;

        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);

        Courier courierUpdate = new Courier();
        courierUpdate.setName("UpdatedName");


        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());


        StatusException exception = assertThrows(StatusException.class,
                () -> courierService.updateCourier(courierId, userId, courierUpdate));

        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, never()).save(any(Courier.class));
        verify(courierRepository, never()).flush();


        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());

    }

    @Test
    public void testUpdateCourierActivationStatus_Success_AdminAuthorized() {

        Long courierId = 1L;
        Long userId = 2L;
        Boolean activationStatus = true;

        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.ADMIN);


        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));


        ResponseEntity<Void> response = courierService.updateCourierActivationStatus(courierId, userId, activationStatus);


        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, times(1)).save(mockCourier);
        verify(courierRepository, times(1)).flush();


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        assertEquals(activationStatus, mockCourier.getActivated());
    }

    @Test
    public void testUpdateCourierActivationStatus_NotFound() {

        Long courierId = 1L;
        Long userId = 2L;
        Boolean activationStatus = true;


        when(courierRepository.findById(courierId)).thenReturn(Optional.empty());


        StatusException exception = assertThrows(StatusException.class,
                () -> courierService.updateCourierActivationStatus(courierId, userId, activationStatus));


        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, never()).save(any(Courier.class));
        verify(courierRepository, never()).flush();


        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

    }
    @Test
    public void testUpdateCourierActivationStatus_Unauthorized() {

        Long courierId = 1L;
        Long userId = 3L;
        Boolean activationStatus = true;

        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);


        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());


        StatusException exception = assertThrows(StatusException.class,
                () -> courierService.updateCourierActivationStatus(courierId, userId, activationStatus));


        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, never()).save(any(Courier.class));
        verify(courierRepository, never()).flush();


        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());

    }

    @Test
    public void testUpdateCourierActivationStatus_NonAdminUser() {

        Long courierId = 1L;
        Long userId = 4L;
        Boolean activationStatus = true;

        Courier mockCourier = new Courier();
        mockCourier.setId(courierId);

        User mockUser = new User();
        mockUser.setRole(User.RoleEnum.COURIER);
        mockUser.setId(userId);


        when(courierRepository.findById(courierId)).thenReturn(Optional.of(mockCourier));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));


        StatusException exception = assertThrows(StatusException.class,
                () -> courierService.updateCourierActivationStatus(courierId, userId, activationStatus));


        verify(courierRepository, times(1)).findById(courierId);
        verify(courierRepository, never()).save(any(Courier.class));
        verify(courierRepository, never()).flush();


        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());

    }

}