package nl.tudelft.sem.yumyumnow.users.services;
import com.sun.xml.bind.v2.TODO;
import nl.tudelft.sem.yumyumnow.users.builders.Builder;
import nl.tudelft.sem.yumyumnow.users.builders.Director;

import nl.tudelft.sem.yumyumnow.users.builders.Ubuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.ValidatorConstructor;
import java.util.List;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.validation.constraints.NotNull;
import java.util.Optional;
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    Builder builder;
    Director director;
     ValidatorConstructor validatorConstructor;



    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.director = new Director();
        this.builder = new Ubuilder();
        this.validatorConstructor = new ValidatorConstructor();
    }
    /**
     * Creates a customer.
     *
     * @param customerRequestModel the customer request model
     * @return the response entity
     */
    public Customer createCustomer (@NotNull CustomerRequestModel customerRequestModel)
            throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException,
            UserNotFoundException, NotAuthorizedException{
        // Little demo
        Customer customer = new Customer();
        //Makes sure we have a correct request model
        nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.Validator validator = validatorConstructor.userWithoutAuthorization();
        validator.handle(new ValidationObject(customerRequestModel, null));

        director.constructCustomer(builder, customerRequestModel);
        Customer cUpdated = (Customer)builder.build(customer);

        if (userRepository.findByEmail(customerRequestModel.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException(customerRequestModel.getEmail());
        }

        customerRepository.saveAndFlush(cUpdated);
        return cUpdated;
    }

    /**
     * Returns a customer model given an ID.
     *
     * @param userId customerId of the customer you want
     * @return Customer model corresponding to the given customer id
     */
    public Customer getCustomerById(Long userId) throws UserNotFoundException {
        Optional<Customer> customer = customerRepository.findById(userId);

        if(customer.isEmpty()) throw new UserNotFoundException();
        else return customer.get();

    }



    public void deleteCustomer (Long userId1, Long userId2)
            throws NotAuthorizedException, UserNotFoundException{
    Customer customer;
    customer = getCustomerById(userId1);

        if(customer.getId().equals(userId2)){
        customerRepository.deleteById(userId1);
        customerRepository.flush();
        return;
    }

    Optional<User> admin = userRepository.findById(userId2);
        if(admin.isEmpty()){
        throw new UserNotFoundException();
    }
    User user = admin.get();
        if(!user.getRole().equals(User.RoleEnum.ADMIN)){
        throw new NotAuthorizedException();
    }
        System.out.println(user);
        customerRepository.deleteById(userId1);
        customerRepository.flush();

}

    public List<Customer> getAllCustomers () {
        return customerRepository.findAll();
    }
     /**
         * Updates the given customer with the given customer request model. In case of invalid authorization, request model
         * or customerId, an exception is thrown.
         *
         * @param id1 ID of the customer you want to update
         * @param id2 ID of the user making the request
         * @param customerRequestModel customer data model that you want to replace the customer denoted by id1 with
         * return void
         *
         */
        public void updateCustomerById (Long id1, Long id2, CustomerRequestModel customerRequestModel)
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
            Customer customer;
            //Makes sure we have a correct request model
            if(customerRequestModel == null){
                throw new MissingBodyException();
            }
            if(customerRequestModel.getSurname() == ""){
                throw new InvalidFieldException("Email", "2");
            }
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

        public void updateFavRestaurants(Long id1, Long id2, List<Long> favRestaurants)
                throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
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


    public Long getPastSavedOrderIdForUser(Long userId) throws UserNotFoundException {

        Optional<Customer> customer = customerRepository.findById(userId);
        if(customer.isEmpty()) throw new UserNotFoundException();
        else return customer.get().getSavedOrder();
    }

    public void updatePastSavedOrderIdForUser(Long userId1, Long userId2, Long orderId)
            throws UserNotFoundException, NotAuthorizedException {

        Customer customer;
        customer = getCustomerById(userId1);

        if(customer.getId().equals(userId2)){
            customer.setSavedOrder(orderId);
            return;
        }

        Optional<User> admin = userRepository.findById(userId2);
        if(admin.isEmpty()){
            throw new UserNotFoundException();
        }
        User user = admin.get();
        if(!user.getRole().equals(User.RoleEnum.ADMIN)){
            throw new NotAuthorizedException();
        }

        customer.setSavedOrder(orderId);
    }


    public void updateCustomerBlockStatus(Long userId1, Long userId2, Boolean body)
            throws UserNotFoundException, NotAuthorizedException {

        Customer customer;
        customer = getCustomerById(userId1);

        Optional<User> user = userRepository.findById(userId2);
        if(user.isEmpty()) throw new NotAuthorizedException();
        User admin = user.get();
        if(admin.getRole().equals(User.RoleEnum.ADMIN)){
            customer.setBlocked(body);
            customerRepository.saveAndFlush(customer);
            return;
        }
        else throw new NotAuthorizedException();
    }


}

