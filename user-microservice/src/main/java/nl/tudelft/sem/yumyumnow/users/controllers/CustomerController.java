package nl.tudelft.sem.yumyumnow.users.controllers;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.yumyumnow.users.api.CustomerApi;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CustomerController implements CustomerApi {


    private final transient CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public ResponseEntity<Customer> createCustomer(CustomerRequestModel customerRequestModel) {

        Customer customer = new Customer();

        // TODO decide over some kind of customer builder with the register model as an argument
        customer.setAllergens(customerRequestModel.getAllergens());
        customer.setBlocked(false);
        customer.setHomeAddress(customerRequestModel.getHomeAddress());
        customer.setFavRestaurants(new ArrayList<>());
        customer.setPaymentMethods(customerRequestModel.getPaymentMethods());
        customer.setName(customerRequestModel.getName());
        customer.setSurname(customerRequestModel.getSurname());
        customer.setRole(User.RoleEnum.CUSTOMER);
        customer.setEmail(customerRequestModel.getEmail());

        customerRepository.saveAndFlush(customer);

        return ResponseEntity.ok(customer);

    }

    @Override
    public ResponseEntity<Customer> getCustomerById(Long userId) {
        return customerRepository.findById(userId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }
}
