package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import nl.tudelft.sem.yumyumnow.users.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class CustomerControllerTest {
    private CustomerController customerController;
    private CustomerService customerService;
    private Customer c1;
    private Customer c2;
    private CustomerRequestModel customerUpdate;

    private Address adr;
    private List<Long> favs;
    @BeforeEach
    public void setUp(){
        customerService = mock(CustomerService.class);
        customerController = new CustomerController(customerService);
    }

    @BeforeEach
    public void setUpEntities(){
        c1 = new Customer();
        c1.setSurname("surname 1");
        c1.setName("name 1");
        c1.setEmail("email 1");
        c1.setRole(User.RoleEnum.ADMIN);
        c1.setId(0L);

        c2 = new Customer();
        c2.setSurname("surname 2");
        c2.setEmail("email 2");
        c2.setRole(User.RoleEnum.ADMIN);
        c2.setName("name 2");
        c2.setId(1L);
        Location l = new Location();
        l.lat(12.1f);
        l._long(6.9f);
        adr = new Address();
        adr.setCoordinates(l);
        favs = new ArrayList<>();
        favs.add(1L);
        favs.add(6L);
        favs.add(10L);

        customerUpdate = new CustomerRequestModel();
        customerUpdate.setName("UpdatedName");
        customerUpdate.setName("UpdatedName");
        customerUpdate.setEmail("email");
        customerUpdate.setAllergens(new ArrayList<>());
        customerUpdate.setSurname("Hi");
        customerUpdate.setHomeAddress(new Address());
    }

    @Test
    public void testGetCustomerByIDExisting() throws UserNotFoundException {
        when(customerService.getCustomerById(0L)).thenReturn(c1);
        ResponseEntity<Customer> c = customerController.getCustomerById(0L);
        assertEquals(c.getBody(), c1);
    }

    @Test
    public void testGetCustomerByIDNotExisting() throws UserNotFoundException {
        when(customerService.getCustomerById(10L)).thenThrow(new UserNotFoundException());
        assertThrows(ResponseStatusException.class, ()->{
            customerController.getCustomerById(10L);
        });
    }

    @Test
    public void testUpdateCustomerWorking() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doNothing().when(customerService).updateCustomerById(1L, 1L, customerUpdate);

        assertThat(customerController.updateCustomer(1L, 1L, customerUpdate))
                .isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        verify(customerService).updateCustomerById(1L, 1L, customerUpdate);
    }

    @Test
    public void testUpdateAdminNullUpdate()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        doThrow(new MissingBodyException()).when(customerService)
                .updateCustomerById(3L, 0L, null);
        assertThrows(ResponseStatusException.class, () -> {
            customerController.updateCustomer(3L, 0L, null);
        });
        try {
            customerController.updateCustomer(3L, 0L, null);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Body not present");
        }
    }

    @Test
    public void testUpdateAdminNoEmail()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {

        customerUpdate.setEmail("");

        doThrow(new InvalidFieldException("email", "")).when(
                customerService).updateCustomerById(3L, 0L, customerUpdate);
        assertThrows(ResponseStatusException.class, () -> {
            customerController.updateCustomer(3L, 0L, customerUpdate);
        });
        try {
            customerController.updateCustomer(3L, 0L, customerUpdate);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Field 'email' with value '' is invalid");
        }
    }

    @Test
    public void testUpdateCustomerUnauthorized()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {

        doThrow(new NotAuthorizedException()).when(
                customerService).updateCustomerById(3L, 1L, customerUpdate);
        assertThrows(ResponseStatusException.class, () -> {
            customerController.updateCustomer(3L, 1L, customerUpdate);
        });
        try {
            customerController.updateCustomer(3L, 1L, customerUpdate);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(403);
            assertThat(e.getReason()).isEqualTo("Unauthorized action");
        }
    }

    @Test
    public void testGetLocationByIDExisting() throws UserNotFoundException {
        when(customerService.getLocation(0L)).thenReturn(adr.getCoordinates());
        ResponseEntity<Location> c = customerController.getCurrentLocationForUser(0L);
        assertEquals(c.getBody(), adr.getCoordinates());
    }

    @Test
    public void testGetLocationByIDNotExisting() throws UserNotFoundException {
        when(customerService.getLocation(10L)).thenThrow(new UserNotFoundException());
        assertThrows(ResponseStatusException.class, ()->{
            customerController.getCurrentLocationForUser(10L);
        });
    }

    @Test
    public void testGetFavsByIDExisting() throws UserNotFoundException {
        when(customerService.getFavoriteRestaurants(0L)).thenReturn(favs);
        ResponseEntity<List<Long>> c = customerController.getFavouriteVendorIdsForCustomer(0L);
        assertEquals(c.getBody(), favs);
    }

    @Test
    public void testGetFavsByIDNotExisting() throws UserNotFoundException {
        when(customerService.getFavoriteRestaurants(10L)).thenThrow(new UserNotFoundException());
        assertThrows(ResponseStatusException.class, ()->{
            customerController.getFavouriteVendorIdsForCustomer(10L);
        });
    }

    @Test
    public void testUpdateFavsWorking() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doNothing().when(customerService).updateFavRestaurants(1L, 1L, favs);

        assertThat(customerController.updateFavouriteVendorIdsForCustomer(1L, 1L, favs))
                .isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        verify(customerService).updateFavRestaurants(1L, 1L, favs);
    }

    @Test
    public void testUpdateFavsNullUpdate()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        doThrow(new MissingBodyException()).when(customerService)
                .updateFavRestaurants(3L, 0L, null);
        assertThrows(ResponseStatusException.class, () -> {
            customerController.updateFavouriteVendorIdsForCustomer(3L, 0L, null);
        });
        try {
            customerController.updateFavouriteVendorIdsForCustomer(3L, 0L, null);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Body not present");
        }
    }


    @Test
    public void testUpdateFavsUnauthorized()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {

        doThrow(new NotAuthorizedException()).when(
                customerService).updateFavRestaurants(3L, 1L, favs);
        assertThrows(ResponseStatusException.class, () -> {
            customerController.updateFavouriteVendorIdsForCustomer(3L, 1L, favs);
        });
        try {
            customerController.updateFavouriteVendorIdsForCustomer(3L, 1L, favs);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(403);
            assertThat(e.getReason()).isEqualTo("Unauthorized action");
        }
    }






}