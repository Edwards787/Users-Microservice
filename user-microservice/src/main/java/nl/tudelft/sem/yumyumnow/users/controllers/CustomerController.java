package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.CustomerApi;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

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
}
