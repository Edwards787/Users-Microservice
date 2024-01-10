package nl.tudelft.sem.yumyumnow.users.services;



import nl.tudelft.sem.yumyumnow.users.builders.CustomerBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validators.CustomerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
    CustomerValidator customerValidator;
    CustomerRepository customerRepository;
    UserRepository userRepository;
    private CustomerBuilder customerBuilder;
    private CustomerService customerService;
    @BeforeEach
    public void setUp(){
        customerValidator = new CustomerValidator();
         customerRepository = mock(CustomerRepository.class);
         userRepository  = mock(UserRepository.class);
         customerBuilder = new CustomerBuilder();
          customerService = new CustomerService(customerRepository, userRepository, customerValidator, customerBuilder);
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
        verify(customerRepository, times(1)).saveAndFlush(customerBuilder.build(mockCustomer, customerUpdate));

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
    public void createCustomer() throws MissingBodyException, InvalidFieldException{
        Long customerId = 1L;
        CustomerRequestModel customer = new CustomerRequestModel();
        customer.setName("UpdatedName");
        customer.setSurname("UpdatedName");
        customer.setEmail("email");
        customer.setAllergens(new ArrayList<>());
        customer.setHomeAddress(new Address());
        Customer k = new Customer();
        Customer cust = customerBuilder.build(k, customer);
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


}