package nl.tudelft.sem.yumyumnow.users.services;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.yumyumnow.users.builders.Builder;
import nl.tudelft.sem.yumyumnow.users.builders.Director;
import nl.tudelft.sem.yumyumnow.users.builders.Ubuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.Accessibility;
import nl.tudelft.sem.yumyumnow.users.validation.objects.AuthorizationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.Validator;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidatorConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CustomerService {

    private final transient CustomerRepository customerRepository;

    private final UserRepository userRepository;
    Builder builder;
    Director director;
    ValidatorConstructor validatorConstructor;

    /**
     * Customer service constructor.
     *
     * @param customerRepository customer repository
     * @param userRepository     user repository
     */
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
    public Customer createCustomer(@NotNull CustomerRequestModel customerRequestModel)
            throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException,
            UserNotFoundException, NotAuthorizedException {

        Validator validator = validatorConstructor.customerWithoutAuthorization();
        validator.handle(new ValidationObject(customerRequestModel, null));

        if (userRepository.findByEmail(customerRequestModel.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException(customerRequestModel.getEmail());
        }

        director.constructCustomer(builder, customerRequestModel);
        Customer customerUpdated = (Customer) builder.build(new Customer());

        customerRepository.saveAndFlush(customerUpdated);
        return customerUpdated;
    }

    /**
     * Returns a customer model given an ID.
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

    /**
     * Service that handles the deletion of a customer by id.
     *
     * @param userId1 user that is deleted
     * @param userId2 user that does the deleting
     * @throws NotAuthorizedException throw exception in case the user with adminId2 is not an admin
     * @throws UserNotFoundException  throw exception in case the user is not found
     * @throws MissingBodyException   throw exception in case the body is missing
     * @throws InvalidFieldException  throw exception in case invalid fields were provided
     */
    public void deleteCustomer(Long userId1, Long userId2)
            throws NotAuthorizedException, UserNotFoundException, MissingBodyException,
            InvalidFieldException {

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null,
                new AuthorizationObject(userId1, userId2,
                        userRepository, Accessibility.USER_AND_ADMIN)));

        customerRepository.deleteById(userId1);
        customerRepository.flush();
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Updates the given customer with the given customer request model. In case of invalid authorization, request model
     * or customerId, an exception is thrown.
     *
     * @param id1                  ID of the customer you want to update
     * @param id2                  ID of the user making the request
     * @param customerRequestModel customer data model that you want to replace the customer denoted by id1 with
     *                             return void
     */
    public void updateCustomerById(Long id1, Long id2, CustomerRequestModel customerRequestModel)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {

        Validator validator = validatorConstructor.customerWithAuthorization();
        validator.handle(new ValidationObject(customerRequestModel, new AuthorizationObject(id1,
                id2, userRepository, Accessibility.USER_AND_ADMIN)));

        //Make sure that we can find the customer to be updated
        Customer customer = getCustomerById(id1);
        director.constructCustomer(builder, customerRequestModel);

        //Set the right attributes for customer
        Customer customerUpdated = (Customer) builder.build(customer);
        customerRepository.saveAndFlush(customerUpdated);
    }

    /**
     * Service that handles the retrieval of the customer location.
     *
     * @param userId the id of the customer
     * @return the coordinates of his location
     * @throws UserNotFoundException throw exception in case the user is not found
     */
    public Location getLocation(Long userId) throws UserNotFoundException {
        Optional<Customer> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            return customer.get().getHomeAddress().getCoordinates();
        }
    }

    /**
     * Service that handles the retrieval of the customer's favourite restaurants.
     *
     * @param userId id of the customer
     * @return a list of the customer's favourite restaurant ids
     * @throws UserNotFoundException throw exception in case the user is not found
     */
    public List<Long> getFavoriteRestaurants(Long userId) throws UserNotFoundException {
        Optional<Customer> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            return customer.get().getFavRestaurants();
        }
    }

    /**
     * Service that handles the update of the customer's favourite restaurants.
     *
     * @param id1            id of the customer whose list is updated
     * @param id2            id of the customer who does the updating of the list
     * @param favRestaurants list of the id's of the new favourite restaurants
     * @throws NotAuthorizedException throw exception in case the user with adminId2 is not an admin
     * @throws UserNotFoundException  throw exception in case the user is not found
     * @throws MissingBodyException   throw exception in case the body is missing
     * @throws InvalidFieldException  throw exception in case invalid fields were provided
     */
    public void updateFavRestaurants(Long id1, Long id2, List<Long> favRestaurants)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationObject(id1,
                id2, userRepository, Accessibility.USER_AND_ADMIN)));

        Customer customer = getCustomerById(id1);
        if (favRestaurants == null) {
            throw new MissingBodyException();
        }

        //Set the right attributes for customer
        customer.setFavRestaurants(favRestaurants);
        customerRepository.saveAndFlush(customer);
    }

    /**
     * Service that handles the retrieval of the customer's past saved order.
     *
     * @param userId id of the customer
     * @return the past saved order's id
     * @throws UserNotFoundException throw exception in case the user is not found
     */
    public Long getPastSavedOrderIdForUser(Long userId) throws UserNotFoundException {

        Optional<Customer> customer = customerRepository.findById(userId);
        if (customer.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            return customer.get().getSavedOrder();
        }
    }

    /**
     * Service that handles the update of the customer's past saved order.
     *
     * @param userId1 id of customer whose past saved order is updated
     * @param userId2 id of user whose doing the updating
     * @param orderId id of the new saved order
     * @throws UserNotFoundException  throw exception in case the user is not found
     * @throws NotAuthorizedException throw exception in case the user is not authorized
     * @throws MissingBodyException   throw exception in case a body is missing
     * @throws InvalidFieldException  throw exception in case invalid fields were provided
     */
    public void updatePastSavedOrderIdForUser(Long userId1, Long userId2, Long orderId)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationObject(userId1,
                userId2, userRepository, Accessibility.USER_AND_ADMIN)));

        Customer customer = getCustomerById(userId1);
        customer.setSavedOrder(orderId);
    }

    /**
     * Service that handles the update of the customer's block status.
     *
     * @param userId1 id of user who we are updating the block status
     * @param userId2 id of user who is doing the updating of the status
     * @param body    if we are blocking or unblocking a user (true/false)
     * @throws UserNotFoundException throw exception in case the user is not found
     * @throws NotAuthorizedException throw exception in case the user is not authorized
     * @throws InvalidFieldException throw exception in case invalid fields were provided
     * @throws MissingBodyException throw exception in case a body is missing
     */
    public void updateCustomerBlockStatus(Long userId1, Long userId2, Boolean body)
            throws UserNotFoundException, NotAuthorizedException, InvalidFieldException,
            MissingBodyException {

        if (body == null) {
            throw new InvalidFieldException("value", "null");
        }

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationObject(userId1,
                userId2, userRepository, Accessibility.ADMIN_ONLY)));

        Customer customer = getCustomerById(userId1);
        customer.setBlocked(body);
        customerRepository.saveAndFlush(customer);
    }
}

