package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

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

    public ResponseEntity<Void> deleteCustomer(Long userId1, Long userId2) {
        Optional<Customer> customer = customerRepository.findById(userId1);

        if (customer.isEmpty()) throw new StatusException(HttpStatus.NOT_FOUND, "No customer with such id");
        Customer to_be_deleted = customer.get();
        if (to_be_deleted.getId().equals(userId2)) {
            customerRepository.deleteById(userId1);
            customerRepository.flush();
            return ResponseEntity.ok().build();
        }

        Optional<User> admin = userRepository.findById(userId2);
        if (admin.isEmpty()) {
            throw new StatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED need to be user or admin");
        }

        User usr = admin.get();
        if (usr.getRole().equals(User.RoleEnum.ADMIN)) {
            customerRepository.deleteById(userId1);
            customerRepository.flush();
            return ResponseEntity.ok().build();
        }
        throw new StatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED need to be user or admin");
    }

    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }

    public Customer getCustomerById(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw new StatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        return customer.get();
    }

    public ResponseEntity<Void> updateCustomerById(Long id1, Long id2, CustomerRequestModel customerRequestModel) {
        Customer customer;
        //Makes sure we have a correct request model
        if (customerRequestModel == null) {
            throw new StatusException(HttpStatus.BAD_REQUEST, "No new customer model present");
        }
        if (customerRequestModel.getAllergens() == null) {
            throw new StatusException(HttpStatus.BAD_REQUEST, "No value for allergens");
        }
        if (customerRequestModel.getName() == null) {
            throw new StatusException(HttpStatus.BAD_REQUEST, "No name in request model");
        }
        if (customerRequestModel.getEmail() == null) {
            throw new StatusException(HttpStatus.BAD_REQUEST, "No valid email given");
        }
        if (customerRequestModel.getSurname() == null) {
            throw new StatusException(HttpStatus.BAD_REQUEST, "No valid surname given");
        }
        if (customerRequestModel.getHomeAddress() == null) {
            throw new StatusException(HttpStatus.BAD_REQUEST, "No valid address given");
        }

        //Make sure that we can find the customer to be updated
        try {
            customer = getCustomerById(id1);
        } catch (StatusException e) {
            throw new StatusException(HttpStatus.valueOf(404), "Customer not Found");
        }
        // In case the customer is updating their own account
        if (id1.equals(id2)) {
            customer.setAllergens(customerRequestModel.getAllergens());
            customer.setEmail(customerRequestModel.getEmail());
            customer.setName(customerRequestModel.getName());
            customer.setSurname(customerRequestModel.getSurname());
            customer.setHomeAddress(customerRequestModel.getHomeAddress());

            customerRepository.save(customer);
            customerRepository.flush();
            return ResponseEntity.ok().build();
        }

        //ensures that the user sending the request otherwise would be an admin
        Optional<User> admin = userRepository.findById(id2);
        if (admin.isEmpty()) {
            throw new StatusException(HttpStatus.UNAUTHORIZED, "Need to be customer or admin to do this");
        }
        User usr = admin.get();
        if (!usr.getRole().equals(User.RoleEnum.ADMIN)) {
            throw new StatusException(HttpStatus.UNAUTHORIZED, "Need to be customer or admin to do this");
        }

        //Set the right attributes for customer
        customer.setAllergens(customerRequestModel.getAllergens());
        customer.setEmail(customerRequestModel.getEmail());
        customer.setName(customerRequestModel.getName());
        customer.setSurname(customerRequestModel.getSurname());
        customer.setHomeAddress(customerRequestModel.getHomeAddress());

        customerRepository.save(customer);
        customerRepository.flush();
        return ResponseEntity.ok().build();
    }


}
