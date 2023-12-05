package nl.tudelft.sem.yumyumnow.users.controllers;

import java.util.ArrayList;
import nl.tudelft.sem.yumyumnow.users.api.CustomerApi;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRegisterRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRepository;
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
    public ResponseEntity<Customer> createCustomer(CustomerRegisterRequestModel customerRegisterRequestModel) {

        Customer customer = new Customer();

        // TODO decide over some kind of customer builder with the register model as an argument
        customer.setAllergens(customerRegisterRequestModel.getAllergens());
        customer.setBlocked(false);
        customer.setHomeAddress(customerRegisterRequestModel.getHomeAddress());
        customer.setFavRestaurants(new ArrayList<>());
        customer.setPaymentMethod(customerRegisterRequestModel.getPaymentMethod());
        customer.setName(customerRegisterRequestModel.getName());
        customer.setSurname(customerRegisterRequestModel.getSurname());
        customer.setRole(User.RoleEnum.CUSTOMER);
        customer.setEmail(customerRegisterRequestModel.getEmail());

        customerRepository.saveAndFlush(customer);

        return ResponseEntity.ok(customer);

    }

    @Override
    public ResponseEntity<Customer> customerUserIdGet(Long userId) {
        return customerRepository.findById(userId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
