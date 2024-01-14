package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.Builder;
import nl.tudelft.sem.yumyumnow.users.builders.Director;
import nl.tudelft.sem.yumyumnow.users.builders.Ubuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    Customer mockCustomer;
    Long customerId;
    CustomerRequestModel customerUpdate;
    Address address;
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

        customerId = 1L;
        mockCustomer = new Customer();
        mockCustomer.setId(customerId);
        mockCustomer.setName("John");
        mockCustomer.setSurname("Doe");
        mockCustomer.setEmail("john.doe@example.com");

        customerUpdate = new CustomerRequestModel();
        customerUpdate.setName("UpdatedName");
        customerUpdate.setEmail("email@g");
        customerUpdate.setAllergens(new ArrayList<>());
        customerUpdate.setSurname("Hello");
        customerUpdate.setHomeAddress(new Address());

        address = new Address();
        address.setAddress("a");
        address.setCity("c");
        address.setCountry("d");
        address.setPostalCode("df");
        Location location = new Location();
        location.setLat(0.1F);
        location.setLong(0.2F);
        address.setCoordinates(location);
    }
    @Test
    public void testGetCustomerByIDExisting() throws UserNotFoundException {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        Customer response = customerService.getCustomerById(customerId);
        verify(customerRepository, times(1)).findById(customerId);
        assertEquals(mockCustomer, response);
    }

    @Test
    public void testGetCustomerByIDNonExisting(){
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
        Long userId = 1L;
        customerUpdate.setHomeAddress(address);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        customerService.updateCustomerById(customerId, userId, customerUpdate);
        verify(customerRepository, times(1)).findById(customerId);

        assertEquals("UpdatedName", mockCustomer.getName());
    }

    @Test
    public void testCustomerUpdateAdminWorking()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException{
        Long userId = 2L;

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.ADMIN);
        customerUpdate.setHomeAddress(address);

        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
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

        customerUpdate.setSurname("");
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
        mockCustomer.setId(1L);

       assertThrows(UserNotFoundException.class, () -> customerService.updateCustomerById(customerId, userId, customerUpdate));
    }

    @Test
    public void testCustomerUpdateUnauthorized(){
        Long userId = 2L;
        Customer mockCustomer = new Customer();
        mockCustomer.setId(customerId);

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.COURIER);

        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));

        assertThrows(NotAuthorizedException.class, () -> customerService.updateCustomerById(customerId, userId, customerUpdate));
    }

    @Test
    void deleteCustomerBySelfSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        Long userId1 = 1L;
        Long userId2 = 1L; // Same as userId1

        Customer customer = new Customer();
        customer.setId(userId1);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(userId1);
        doNothing().when(customerRepository).flush();

        customerService.deleteCustomer(userId1, userId2);

        verify(customerRepository, times(1)).deleteById(userId1);
        verify(customerRepository, times(1)).flush();
    }

    @Test
    void deleteCustomerByAdminSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        Long userId1 = 1L;
        Long userId2 = 2L;
        User adminUser = new User();
        adminUser.setRole(User.RoleEnum.ADMIN);

        Customer customer = new Customer();
        customer.setId(userId1);

        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId1)).thenReturn(Optional.of(customer));
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(customer));
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
        Long userId = 2L;

        User mockAdmin = new User();
        mockAdmin.setRole(User.RoleEnum.COURIER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));
        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        assertThrows(NotAuthorizedException.class, () -> customerService.updateCustomerById(customerId, userId, customerUpdate));
    }
    @Test
    public void createCustomer() throws MissingBodyException, InvalidFieldException,
            NotAuthorizedException, UserNotFoundException, EmailAlreadyInUseException {
        Customer k = new Customer();

        director.constructCustomer(builder, customerUpdate);
        Customer cust = (Customer) builder.build(k);

        cust.setHomeAddress(address);
        customerUpdate.setHomeAddress(address);

        Customer c = customerService.createCustomer(customerUpdate);
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
        mockCustomer.setHomeAddress(adr);
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        mockCustomer.setFavRestaurants(favs);
        List<Long> response = customerService.getFavoriteRestaurants(customerId);

        verify(customerRepository, times(1)).findById(customerId);
        assertEquals(favs, response);
    }

    @Test
    public void testGetFavsNotWorking() {
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

        User mockAdmin = new User();
        mockAdmin.setId(userId);
        mockAdmin.setRole(User.RoleEnum.ADMIN);

        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
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


        assertThrows(MissingBodyException.class, () -> customerService.updateFavRestaurants(customerId, userId, null));
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

        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));

        assertThrows(NotAuthorizedException.class, () -> customerService.updateFavRestaurants(customerId, userId, favs));
    }

    @Test
    public void testFavUpdateAdminNotThere(){
        Long customerId = 1L;
        Long userId = 2L;

        User mockAdmin = new User();
        mockAdmin.setRole(User.RoleEnum.COURIER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockAdmin));
        when(userRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));

        assertThrows(NotAuthorizedException.class, () -> customerService.updateFavRestaurants(customerId, userId, favs));
    }

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
    void updateCustomerBlockStatusByAdminSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Boolean body = true;

        Customer customer = new Customer();
        customer.setId(userId1);

        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(customer));
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(adminUser));

        assertThrows(NotAuthorizedException.class,
                () -> customerService.updateCustomerBlockStatus(userId1, userId2, body));
    }

    @Test
    void updatePastSavedOrderIdForUserBySelfSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        Long userId1 = 1L;
        Long userId2 = 1L;
        Long orderId = 123L;

        Customer customer = new Customer();
        customer.setId(userId1);
        customer.setSavedOrder(0L);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        customerService.updatePastSavedOrderIdForUser(userId1, userId2, orderId);

        assertThat(customer.getSavedOrder()).isEqualTo(orderId);
    }

    @Test
    void updatePastSavedOrderIdForUserByAdminSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        Long userId1 = 1L;
        Long userId2 = 2L;
        Long orderId = 123L;

        Customer customer = new Customer();
        customer.setId(userId1);
        User adminUser = new User();
        adminUser.setId(userId2);
        adminUser.setRole(User.RoleEnum.ADMIN);

        when(userRepository.findById(userId1)).thenReturn(Optional.of(customer));
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

        when(userRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(customerRepository.findById(userId1)).thenReturn(Optional.of(customer));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(nonAdminUser));

        assertThrows(NotAuthorizedException.class,
                () -> customerService.updatePastSavedOrderIdForUser(userId1, userId2, orderId));
    }
}