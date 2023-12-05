package nl.tudelft.sem.yumyumnow.users.controllers;

import java.util.List;
import nl.tudelft.sem.yumyumnow.users.api.CustomersApi;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CustomersController implements CustomersApi {


    private final transient CustomerRepository customerRepository;

    @Autowired
    public CustomersController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }
}
