package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.CustomerApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.PaymentMethod;
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
        try {
            customerService.deleteCustomer(userId1, userId2);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (StatusException e) {
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<Customer>> getAllCustomers() {
        ResponseEntity<List<Customer>> responseEntity;
        try{
            responseEntity = customerService.getAllCustomers();
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;

    }

}
