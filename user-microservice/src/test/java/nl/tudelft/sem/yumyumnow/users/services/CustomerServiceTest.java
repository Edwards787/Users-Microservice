package nl.tudelft.sem.yumyumnow.users.services;



import nl.tudelft.sem.yumyumnow.users.builders.Builder;
import nl.tudelft.sem.yumyumnow.users.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.users.builders.Director;
import nl.tudelft.sem.yumyumnow.users.builders.Ubuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validators.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.vfs.Vfs;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
    CustomerRepository customerRepository;
    UserRepository userRepository;
    private CustomerService customerService;
    Address adr;
    private List<Long> favs;
    Builder builder;
    Director director;
    @BeforeEach
    public void setUp(){
        customerRepository = mock(CustomerRepository.class);
        userRepository  = mock(UserRepository.class);
        director = new Director();
        builder = new Ubuilder();
        customerService = new CustomerService(customerRepository, userRepository);
        Location l = new Location();
        l.lat(12.1f);
        l._long(6.9f);
        adr = new Address();
        adr.setCoordinates(l);
        favs = new ArrayList<>();
        favs.add(1L);
        favs.add(6L);
        favs.add(10L);
    }
    @Test
    public void testGetCustomerByIDExisting() throws UserNotFoundException {
        Long customerId = 1L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setName("John");
        mockCustomer.setSurname("Doe");
        mockCustomer.setEmail("john.doe@example.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        Customer response = customerService.getCustomerById(customerId);

        verify(customerRepository, times(1)).findById(customerId);

        assertEquals(mockCustomer, response);
    }

    @Test
    public void testGetCustomerByIDNonExisting(){
        Long customerId = 1L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setName("John");
        mockCustomer.setSurname("Doe");
        mockCustomer.setEmail("john.doe@example.com");

        Long cId2 = 2L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        assertThrows(UserNotFoundException.class, () ->{
            customerService.getCustomerById(cId2);
        });

        verify(customerRepository, times(1)).findById(cId2);
    }

    @Test
    void getAllCustomers() {
        customerService.getAllCustomers();
        verify(customerRepository, times(1)).findAll();
    }


    @Test
    public void testUpdateCustomerSelfWorking()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException{
        Long customerId =1L;
        Long userId = 1L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        CustomerRequestModel customerUpdate = new CustomerRequestModel();
        customerUpdate.setName("UpdatedName");
        customerUpdate.setEmail("email");
        customerUpdate.setAllergens(new ArrayList<>());
        customerUpdate.setSurname("Hello");
        customerUpdate.setHomeAddress(new Address());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        customerService.updateCustomerById(customerId, userId, customerUpdate);

        verify(customerRepository, times(1)).findById(customerId);

        assertEquals("UpdatedName", mockCustomer.getName());
    }

    @Test
    public void testCustomerUpdateAdminWorking()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException{
        Long customerId = 1L;
        Long userId = 2L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        CustomerRequestModel customerUpdate = new CustomerRequestModel();
        customerUpdate.setName("UpdatedName");
        customerUpdate.setName("UpdatedName");
        customerUpdate.setEmail("email");
        customerUpdate.setAllergens(new ArrayList<>());
        customerUpdate.setSurname("Hi");
        customerUpdate.setHomeAddress(new Address());

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.ADMIN);


        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));

        customerService.updateCustomerById(customerId, userId, customerUpdate);

        verify(customerRepository, times(1)).findById(customerId);
        director.constructCustomer(builder, customerUpdate);
        verify(customerRepository, times(1)).saveAndFlush((Customer)builder.build(mockCustomer));

        assertEquals("UpdatedName", mockCustomer.getName());
    }

    @Test
    public void testCustomerUpdateInvalidRequestModel(){
        Long customerId = 1L;
        Long userId = 1L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        CustomerRequestModel customerUpdate = new CustomerRequestModel();
        customerUpdate.setName("UpdatedName");
        customerUpdate.setSurname("");
        customerUpdate.setEmail("email");
        customerUpdate.setAllergens(new ArrayList<>());
        customerUpdate.setHomeAddress(new Address());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        assertThrows(InvalidFieldException.class, () -> customerService.updateCustomerById(customerId, userId, customerUpdate));
        CustomerRequestModel crm = null;
        assertThrows(MissingBodyException.class, () -> customerService.updateCustomerById(customerId, userId, crm));

    }

    @Test
    public void testCustomerUpdateInvalidUserID(){
        Long customerId = 3L;
        Long userId = 2L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);

        CustomerRequestModel customerUpdate = new CustomerRequestModel();
        customerUpdate.setName("UpdatedName");
        customerUpdate.setSurname("UpdatedName");
        customerUpdate.setEmail("email");
        customerUpdate.setAllergens(new ArrayList<>());
        customerUpdate.setHomeAddress(new Address());

       assertThrows(UserNotFoundException.class, () -> customerService.updateCustomerById(customerId, userId, customerUpdate));


    }

    @Test
    public void testCustomerUpdateUnauthorized(){
        Long customerId = 1L;
        Long userId = 2L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        CustomerRequestModel customerUpdate = new CustomerRequestModel();
        customerUpdate.setName("UpdatedName");
        customerUpdate.setSurname("UpdatedName");
        customerUpdate.setEmail("email");
        customerUpdate.setAllergens(new ArrayList<>());
        customerUpdate.setHomeAddress(new Address());

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.COURIER);


        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));


        assertThrows(NotAuthorizedException.class, () -> customerService.updateCustomerById(customerId, userId, customerUpdate));

    }

    @Test
    void deleteCustomerBySelfSuccess() throws UserNotFoundException, NotAuthorizedException {
        Long userId1 = 1L;
        Long userId2 = 1L; // Same as userId1

        Customer customer = new Customer();
        customer.setId(userId1);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(userId1);
        doNothing().when(customerRepository).flush();

        customerService.deleteCustomer(userId1, userId2);

        verify(customerRepository, times(1)).deleteById(userId1);
        verify(customerRepository, times(1)).flush();
    }

    @Test
    void deleteCustomerByAdminSuccess() throws UserNotFoundException, NotAuthorizedException {
        Long userId1 = 1L;
        Long userId2 = 2L;
        User adminUser = new User();
        adminUser.setRole(User.RoleEnum.ADMIN);

        Customer customer = new Customer();
        customer.setId(userId1);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));
        doNothing().when(customerRepository).deleteById(userId1);
        doNothing().when(customerRepository).flush();

        customerService.deleteCustomer(userId1, userId2);

        verify(customerRepository, times(1)).deleteById(userId1);
        verify(customerRepository, times(1)).flush();
    }

    @Test
    void deleteCustomerNotAuthorizedException() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        User nonAdminUser = new User();
        nonAdminUser.setRole(User.RoleEnum.CUSTOMER);

        Customer customer = new Customer();
        customer.setId(userId1);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(nonAdminUser));

        assertThrows(NotAuthorizedException.class,
                () -> customerService.deleteCustomer(userId1, userId2));
    }

    @Test
    void deleteAdminExceptionUserNotFound() throws UserNotFoundException, NotAuthorizedException {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long orderId = 123L;

        Customer customer = new Customer();
        customer.setId(userId1);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> customerService.deleteCustomer(userId1, userId2));
    }

    @Test
    public void testCustomerUpdateAdminNotThere(){
        Long customerId = 1L;
        Long userId = 2L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        CustomerRequestModel customerUpdate = new CustomerRequestModel();
        customerUpdate.setName("UpdatedName");
        customerUpdate.setSurname("UpdatedName");
        customerUpdate.setEmail("email");
        customerUpdate.setAllergens(new ArrayList<>());
        customerUpdate.setHomeAddress(new Address());

        User mockAdmin = new User();
        mockAdmin.setRole(User.RoleEnum.COURIER);


        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));


        assertThrows(NotAuthorizedException.class, () -> customerService.updateCustomerById(customerId, userId, customerUpdate));

    }
    @Test
    public void createCustomer() throws MissingBodyException, InvalidFieldException,
            NotAuthorizedException, UserNotFoundException, EmailAlreadyInUseException {
        Long customerId = 1L;
        CustomerRequestModel customer = new CustomerRequestModel();
        customer.setName("UpdatedName");
        customer.setSurname("UpdatedName");
        customer.setEmail("email1@mail.com");
        customer.setAllergens(new ArrayList<>());
        customer.setHomeAddress(new Address());
        Customer k = new Customer();
        director.constructCustomer(builder, customer);
        Customer cust = (Customer) builder.build(k);
        Customer c = customerService.createCustomer(customer);
        assertEquals(c, cust);
        verify(customerRepository).saveAndFlush(cust);
    }


    @Test
    public void createCustomerException(){
        Long customerId = 1L;
        CustomerRequestModel customer =null;
        assertThrows(MissingBodyException.class, () ->{
           customerService.createCustomer(customer);
        });
    }

    @Test
    public void createCustomerException2(){
        Long customerId = 1L;
        CustomerRequestModel customer =new CustomerRequestModel();
        assertThrows(InvalidFieldException.class, () ->{
            customerService.createCustomer(customer);
        });
    }

    @Test
    public void testGetLocation()throws UserNotFoundException{
        Long customerId = 1L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setName("John");
        mockCustomer.setSurname("Doe");
        mockCustomer.setEmail("john.doe@example.com");
        mockCustomer.setHomeAddress(adr);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        Location response = customerService.getLocation(customerId);

        verify(customerRepository, times(1)).findById(customerId);

        assertEquals(adr.getCoordinates(), response);
    }

    @Test
    void getLocationUserNotFound() {
        Long userId = 1L;

        when(customerRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> customerService.getLocation(userId));
    }


    @Test
    public void testGetLocationNotWorking() {
        Long customerId = 1L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setName("John");
        mockCustomer.setSurname("Doe");
        mockCustomer.setEmail("john.doe@example.com");
        mockCustomer.setHomeAddress(adr);
        Long cId2 = 2L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        assertThrows(UserNotFoundException.class, () ->{
            customerService.getCustomerById(cId2);
        });

        verify(customerRepository, times(1)).findById(cId2);
    }

    @Test
    public void testGetFavLocations()throws UserNotFoundException{
        Long customerId = 1L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setName("John");
        mockCustomer.setSurname("Doe");
        mockCustomer.setEmail("john.doe@example.com");
        mockCustomer.setHomeAddress(adr);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        mockCustomer.setFavRestaurants(favs);
        List<Long> response = customerService.getFavoriteRestaurants(customerId);

        verify(customerRepository, times(1)).findById(customerId);

        assertEquals(favs, response);
    }

    @Test
    public void testGetFavsNotWorking() {
        Long customerId = 1L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setName("John");
        mockCustomer.setSurname("Doe");
        mockCustomer.setEmail("john.doe@example.com");
        mockCustomer.setHomeAddress(adr);
        Long cId2 = 2L;
        mockCustomer.setFavRestaurants(favs);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        assertThrows(UserNotFoundException.class, () ->{
            customerService.getCustomerById(cId2);
        });

        verify(customerRepository, times(1)).findById(cId2);
    }

    @Test
    public void testFavsUpdateAdminWorking()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException{
        Long customerId = 1L;
        Long userId = 2L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setFavRestaurants(new ArrayList<>());

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.ADMIN);


        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));


        customerService.updateFavRestaurants(customerId, userId, favs);

        verify(customerRepository, times(1)).findById(customerId);

        assertEquals(favs, mockCustomer.getFavRestaurants());
    }


    @Test
    public void testFavsUpdateInvalidList(){
        Long customerId = 1L;
        Long userId = 1L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        List<Long> favs2 = null;
        assertThrows(MissingBodyException.class, () -> customerService.updateFavRestaurants(customerId, userId, favs2));
    }

    @Test
    public void testFavsUpdateInvalidUserID(){
        Long customerId = 3L;
        Long userId = 2L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(1L);

        assertThrows(UserNotFoundException.class, () -> customerService.updateFavRestaurants(customerId, userId, favs));
    }

    @Test
    public void testFavsUpdateUnauthorized(){
        Long customerId = 1L;
        Long userId = 2L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.COURIER);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));

        assertThrows(NotAuthorizedException.class, () -> customerService.updateFavRestaurants(customerId, userId, favs));

    }

    @Test
    public void testFavUpdateAdminNotThere(){
        Long customerId = 1L;
        Long userId = 2L;

        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        User mockAdmin = new User();
        mockAdmin.setRole(User.RoleEnum.COURIER);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        assertThrows(NotAuthorizedException.class, () -> customerService.updateFavRestaurants(customerId, userId, favs));
    }
<<<<<<< HEAD
=======

    @Test
    void getPastSavedOrderIdForUserSuccess() throws UserNotFoundException {
        Long userId = 1L;
        Long expectedOrderId = 123L;

        Customer customer = new Customer();
        customer.setSavedOrder(expectedOrderId);

        when(customerRepository.findById(userId)).thenReturn(Optional.of(customer));

        Long actualOrderId = customerService.getPastSavedOrderIdForUser(userId);

        assertThat(actualOrderId).isEqualTo(expectedOrderId);
    }


    @Test
    void updateCustomerBlockStatusByAdminSuccess() throws UserNotFoundException, NotAuthorizedException {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean body = true;

        Customer customer = new Customer();
        customer.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        customerService.updateCustomerBlockStatus(userId1, userId2, body);

        verify(customerRepository, times(1)).saveAndFlush(customer);
        assertThat(customer.getBlocked()).isEqualTo(body);
    }

    @Test
    void updateCustomerBlockStatusNotAuthorizedExceptionNonAdmin() {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean body = true;

        Customer customer = new Customer();
        customer.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.CUSTOMER);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        assertThrows(NotAuthorizedException.class,
                () -> customerService.updateCustomerBlockStatus(userId1, userId2, body));
    }

    @Test
    void updatePastSavedOrderIdForUserBySelfSuccess() throws UserNotFoundException, NotAuthorizedException {
        Long userId1 = 1L;
        Long userId2 = 1L;
        Long orderId = 123L;

        Customer customer = new Customer();
        customer.setId(userId1);
        customer.setSavedOrder(0L);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));

        customerService.updatePastSavedOrderIdForUser(userId1, userId2, orderId);

        assertThat(customer.getSavedOrder()).isEqualTo(orderId);
    }

    @Test
    void updatePastSavedOrderIdForUserByAdminSuccess() throws UserNotFoundException, NotAuthorizedException {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long orderId = 123L;

        Customer customer = new Customer();
        customer.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        customerService.updatePastSavedOrderIdForUser(userId1, userId2, orderId);

        assertThat(customer.getSavedOrder()).isEqualTo(orderId);
    }

    @Test
    void updatePastSavedOrderIdForUserNotAuthorizedExceptionUserNotFound() throws UserNotFoundException, NotAuthorizedException {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long orderId = 123L;

        Customer customer = new Customer();
        customer.setId(userId1);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> customerService.updatePastSavedOrderIdForUser(userId1, userId2, orderId));
    }


    @Test
    void updatePastSavedOrderIdForUserNotAuthorizedExceptionNonAdmin() throws UserNotFoundException, NotAuthorizedException {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long orderId = 123L;

        Customer customer = new Customer();
        customer.setId(userId1);

        User nonAdminUser = new User();
        nonAdminUser.setId(userId2);
        nonAdminUser.setRole(User.RoleEnum.CUSTOMER);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(nonAdminUser));

        assertThrows(NotAuthorizedException.class,
                () -> customerService.updatePastSavedOrderIdForUser(userId1, userId2, orderId));
    }


>>>>>>> b1043bdfa671584392025ea638dc5912cfd774a4
}