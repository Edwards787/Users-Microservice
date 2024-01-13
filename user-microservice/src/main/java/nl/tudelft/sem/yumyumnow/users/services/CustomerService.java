package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.Builder;
import nl.tudelft.sem.yumyumnow.users.builders.Director;
import nl.tudelft.sem.yumyumnow.users.builders.Ubuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.*;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.ValidatorConstructor;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotNull;
import java.util.List;
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

        Validator validator = validatorConstructor.customerWithoutAuthorization();
        validator.handle(new ValidationObject(customerRequestModel, null));

        if (userRepository.findByEmail(customerRequestModel.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException(customerRequestModel.getEmail());
        }

        director.constructCustomer(builder, customerRequestModel);
        Customer cUpdated = (Customer)builder.build(new Customer());

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
        throws NotAuthorizedException, UserNotFoundException, MissingBodyException,
        InvalidFieldException {

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null,
            new AuthorizationObject(userId1, userId2,
                userRepository, Accessibility.USER_AND_ADMIN)));

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
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        Validator validator = validatorConstructor.customerWithAuthorization();
        validator.handle(new ValidationObject(customerRequestModel, new AuthorizationObject(id1,
            id2, userRepository, Accessibility.USER_AND_ADMIN)));

        //Make sure that we can find the customer to be updated
        Customer customer = getCustomerById(id1);
        director.constructCustomer(builder, customerRequestModel);

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

    public List<Long> getFavoriteRestaurants(Long userId) throws UserNotFoundException{
        Optional<Customer> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            return customer.get().getFavRestaurants();
        }
    }

    public void updateFavRestaurants(Long id1, Long id2, List<Long> favRestaurants)
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationObject(id1,
            id2, userRepository, Accessibility.USER_AND_ADMIN)));

        Customer customer = getCustomerById(id1);
        if(favRestaurants == null){
            throw new MissingBodyException();
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
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationObject(userId1,
            userId2, userRepository, Accessibility.USER_AND_ADMIN)));

        Customer customer = getCustomerById(userId1);
        customer.setSavedOrder(orderId);
    }


    public void updateCustomerBlockStatus(Long userId1, Long userId2, Boolean body)
        throws UserNotFoundException, NotAuthorizedException, InvalidFieldException,
        MissingBodyException {

        if (body == null) throw new InvalidFieldException("value", "null");

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationObject(userId1,
            userId2, userRepository, Accessibility.ADMIN_ONLY)));

        Customer customer = getCustomerById(userId1);
        customer.setBlocked(body);
        customerRepository.saveAndFlush(customer);
    }
}

