package nl.tudelft.sem.yumyumnow.users.services;

import java.util.ArrayList;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class CustomerService {

    private final transient CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Creates a customer.
     *
     * @param customerRequestModel the customer request model
     * @return the response entity
     */
    public ResponseEntity<Customer> createCustomer(CustomerRequestModel customerRequestModel) {
        // Little demo
        Customer customer = new Customer();

        customer.setAllergens(customerRequestModel.getAllergens());
        customer.setBlocked(false);
        customer.setHomeAddress(customerRequestModel.getHomeAddress());
        customer.setFavRestaurants(new ArrayList<>());
        customer.setName(customerRequestModel.getName());
        customer.setSurname(customerRequestModel.getSurname());
        customer.setRole(User.RoleEnum.CUSTOMER);
        customer.setEmail(customerRequestModel.getEmail());

        customerRepository.saveAndFlush(customer);

        return ResponseEntity.ok(customer);

    }
}
