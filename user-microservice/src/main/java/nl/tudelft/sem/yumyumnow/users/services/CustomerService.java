package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.Builder;
import nl.tudelft.sem.yumyumnow.users.builders.Director;

import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validators.CustomerValidator;
import nl.tudelft.sem.yumyumnow.users.validators.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;


import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerValidator customerValidator;
    Builder builder;
    Director director;
    Validator validator;


    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           UserRepository userRepository, CustomerValidator customerValidator, Builder builder, Director director, Validator validator) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.customerValidator = customerValidator;
        this.director = director;
        this.builder = builder;
        this.validator = validator;

    }

    /**
     * Returns a customer model given an Id.
     *
     * @param userId customerId of the customer you want
     * @return Customer model corresponding to the given customer id
     */
    public Customer getCustomerById(Long userId) throws UserNotFoundException {
        Optional<Customer> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            return customer.get();
        }
    }


        public Customer createCustomer (CustomerRequestModel customerRequestModel)throws MissingBodyException, InvalidFieldException{
            // Little demo
            Customer customer = new Customer();
            //Makes sure we have a correct request model
            customerValidator.validate(customerRequestModel);
            director.constructCustomer(builder, customerRequestModel);
            Customer cUpdated = (Customer)builder.build(customer);
            customerRepository.saveAndFlush(customer);
            return customer;
    }

    public ResponseEntity<Void> deleteCustomer (Long userId1, Long userId2)
    throws NotAuthorizedException, UserNotFoundException {
        try {
            customerRepository.deleteById(userId1);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<List<Customer>> getAllCustomers () {
        List<Customer> customers = customerRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }
     /**
         * Updates the given customer with the given customer request model. In case of invalid authorization, request model
         * or customerId, an exception is thrown.
         *
         * @param id1 ID of the customer you want to update
         * @param id2 ID of the user making the request
         * @param customerRequestModel customer data model that you want to replace the customer denoted by id1 with
         * @return void
         *
         */
        public void updateCustomerById (Long id1, Long id2, CustomerRequestModel customerRequestModel)
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
            Customer customer;
            //Makes sure we have a correct request model
            customerValidator.validate(customerRequestModel);
            //Make sure that we can find the customer to be updated
            customer = getCustomerById(id1);
            director.constructCustomer(builder, customerRequestModel);

            // In case the customer is updating their own account
            if (id1.equals(id2)) {
                Customer cUpdated = (Customer) builder.build(customer);
                customerRepository.saveAndFlush(cUpdated);
                return;
            }

            //ensures that the user sending the request otherwise would be an admin
            Optional<User> admin = userRepository.findById(id2);
            if (admin.isEmpty()) {
                throw new NotAuthorizedException();
            }
            User usr = admin.get();
            if (!usr.getRole().equals(User.RoleEnum.ADMIN)) {
                throw new NotAuthorizedException();
            }

            //Set the right attributes for customer

            Customer cUpdated = (Customer) builder.build(customer);
            customerRepository.saveAndFlush(cUpdated);
        }

        public Location getLocation(Long userId) throws UserNotFoundException{
            Optional<Customer> customer = customerRepository.findById(userId);
            if (customer.isEmpty()) {
                throw new UserNotFoundException();
            } else {
                return customer.get().getHomeAddress().getCoordinates();
            }
        }

        public List<Long> getFavoriteRestaurants(Long userId)throws UserNotFoundException{
            Optional<Customer> customer = customerRepository.findById(userId);
            if (customer.isEmpty()) {
                throw new UserNotFoundException();
            } else {
                return customer.get().getFavRestaurants();
            }
        }

        public void updateFavRestaurants(Long id1, Long id2, List<Long> favRestaurants) throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
            Customer customer;
            customer = getCustomerById(id1);
            if(favRestaurants == null){
                throw new MissingBodyException();
            }
            // In case the customer is updating their own account
            if (id1.equals(id2)) {
                customer.setFavRestaurants(favRestaurants);
                customerRepository.saveAndFlush(customer);
                return;
            }

            //ensures that the user sending the request otherwise would be an admin
            Optional<User> admin = userRepository.findById(id2);
            if (admin.isEmpty()) {
                throw new NotAuthorizedException();
            }
            User usr = admin.get();
            if (!usr.getRole().equals(User.RoleEnum.ADMIN)) {
                throw new NotAuthorizedException();
            }

            //Set the right attributes for customer
            customer.setFavRestaurants(favRestaurants);
            customerRepository.saveAndFlush(customer);
        }




}

