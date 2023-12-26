package nl.tudelft.sem.yumyumnow.users.services;


import nl.tudelft.sem.yumyumnow.users.exceptions.InitialCustomerException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validators.CustomerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private final CustomerValidator customerValidator;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository, CustomerValidator customerValidator){
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.customerValidator = customerValidator;
    }

    public Customer getCustomerById(Long userId) throws UserNotFoundException {
        Optional<Customer> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new UserNotFoundException();
        }
        else {
            return customer.get();
        }
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

    public ResponseEntity<Void> deleteCustomer(Long userId1, Long userId2)
        throws InitialCustomerException, NotAuthorizedException, UserNotFoundException {
            if (userId1 == 0)
                throw new InitialCustomerException();
            try{
                Customer customer = getCustomerById(userId2);
            } catch (UserNotFoundException exception){
                throw new NotAuthorizedException();
            }
            try {
                customerRepository.deleteById(userId1);
            } catch (EmptyResultDataAccessException e) {
                throw new UserNotFoundException();
            }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<Customer>> getAllCustomers(){
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }
}
