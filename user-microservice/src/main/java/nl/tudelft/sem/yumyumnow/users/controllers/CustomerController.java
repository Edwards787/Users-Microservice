package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.CustomerApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.InitialAdminException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InitialCustomerException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;

import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class CustomerController implements CustomerApi {


    private final CustomerService customerService;
    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public ResponseEntity<Customer> createCustomer(CustomerRequestModel customerRequestModel) {
        return customerService.createCustomer(customerRequestModel);
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(Long userId1, Long userId2) {
        try{
            customerService.deleteCustomer(userId1, userId2);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        } catch (InitialCustomerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Initial customer can not be deleted");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        }

    }

    @Override
    public ResponseEntity<List<Customer>> getAllCustomers() {
        ResponseEntity<List<Customer>> responseEntity;
            responseEntity = customerService.getAllCustomers();
        return responseEntity;

    }

    @Override
    public ResponseEntity<Customer> getCustomerById(Long userId) {
        try {
            Customer customer = customerService.getCustomerById(userId);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
    }
    
}
