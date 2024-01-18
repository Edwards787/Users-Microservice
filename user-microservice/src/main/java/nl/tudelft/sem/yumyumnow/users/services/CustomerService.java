package nl.tudelft.sem.yumyumnow.users.services;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.factories.CustomerFactory;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.model.PaymentMethod;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.Validator;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidatorConstructor;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationData;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationUserAndAdminStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CustomerService {

    private final transient CustomerRepository customerRepository;
    private final transient UserRepository userRepository;
    private final transient ValidatorConstructor validatorConstructor;
    private final transient HttpService httpService;
    private final transient CustomerFactory customerFactory;

    /**
     * Customer service constructor.
     *
     * @param customerRepository customer repository
     * @param userRepository     user repository
     */
    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           UserRepository userRepository, HttpService httpService,
                           CustomerFactory customerFactory) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.validatorConstructor = new ValidatorConstructor();
        this.customerFactory = customerFactory;
        this.httpService = httpService;
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

        Customer customer = customerFactory.build(customerRequestModel);
        customerRepository.saveAndFlush(customer);

        return customer;
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
                new AuthorizationData(userId1, userId2,
                        userRepository, new AuthorizationUserAndAdminStrategy())));

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
        validator.handle(new ValidationObject(customerRequestModel, new AuthorizationData(id1,
                id2, userRepository, new AuthorizationUserAndAdminStrategy())));

        //Make sure that we can find the customer to be updated
        Customer customer = getCustomerById(id1);

        customer.setName(customerRequestModel.getName());
        customer.setSurname(customerRequestModel.getSurname());
        customer.setEmail(customerRequestModel.getEmail());
        customer.setHomeAddress(customer.getHomeAddress());
        customer.setAllergens(customerRequestModel.getAllergens());

        customerRepository.saveAndFlush(customer);
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
        validator.handle(
                new ValidationObject(
                        null,
                        new AuthorizationData(
                                id1,
                                id2,
                                userRepository,
                                new AuthorizationUserAndAdminStrategy()
                        )));


        if (favRestaurants == null) {
            throw new MissingBodyException();
        }

        //Set the right attributes for customer
        Customer customer = getCustomerById(id1);
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
        validator.handle(new ValidationObject(null, new AuthorizationData(userId1,
                userId2, userRepository, new AuthorizationUserAndAdminStrategy())));

        Customer customer = getCustomerById(userId1);
        customer.setSavedOrder(orderId);
        customerRepository.saveAndFlush(customer);
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
        validator.handle(new ValidationObject(null, new AuthorizationData(userId1,
                userId2, userRepository, new AuthorizationAdminOnlyStrategy())));

        Customer customer = getCustomerById(userId1);
        customer.setBlocked(body);
        customerRepository.saveAndFlush(customer);
    }

    /**
     * Retrieves the current order IDs for a given user.
     *
     * @param userId the unique identifier of the user
     * @return a List of Long representing the current order IDs for the user
     * @throws UserNotFoundException if the user with the given userId is not found
     */
    public List<Long> getCurrentOrderIdsForUser(Long userId) throws UserNotFoundException {
        Optional<Customer> customer = customerRepository.findById(userId);

        // Check if the user exists
        if (customer.isEmpty()) {
            throw new UserNotFoundException();
        }

        // Retrieve current order IDs using the HTTP service
        return httpService.getCurrentOrderIdsForUser("http://localhost:8082", userId);
    }


    /**
     * Requests a refund for a specific order on behalf of a customer identified by their user IDs.
     *
     * @param userId1 the unique identifier of the customer initiating the refund request
     * @param userId2 the unique identifier of the customer on whose behalf the refund is requested
     * @param orderId the unique identifier of the order for which a refund is requested
     * @throws UserNotFoundException if either of the user IDs (userId1 or userId2) is not found
     * @throws NotAuthorizedException if the user initiating the refund (userId1) is not authorized
     * @throws MissingBodyException if the request body is missing or invalid
     * @throws InvalidFieldException if any field in the request body is invalid
     */
    public void requestRefundByCustomerIdForOrderId(Long userId1, Long userId2, Long orderId)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        // Validate user authorization
        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationData(userId1,
                userId2, userRepository, new AuthorizationUserAndAdminStrategy())));

        // Request refund using the HTTP service
        httpService.requestRefundByCustomerIdForOrderId("http://localhost:8082", orderId);
    }


    /**
     * Retrieves the payment methods of a customer.
     *
     * @param issuerId id of the issuer
     * @param customerId id of the customer
     * @return a list of payment methods
     * @throws UserNotFoundException if either of the user IDs (userId1 or userId2) is not found
     * @throws NotAuthorizedException if the user initiating the refund (userId1) is not authorized
     * @throws MissingBodyException if the request body is missing or invalid
     * @throws InvalidFieldException if any field in the request body is invalid
     */
    public List<PaymentMethod> getCustomerPaymentById(Long issuerId, Long customerId)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationData(customerId,
                issuerId, userRepository, new AuthorizationUserAndAdminStrategy())));

        Optional<Customer> optCustomer = customerRepository.findById(customerId);

        if (optCustomer.isEmpty()) {
            throw new UserNotFoundException(customerId);
        }

        return optCustomer.get().getPaymentMethods();
    }

    /**
     * Updates the payment methods of a customer.
     *
     * @param issuerId  id of the issuer
     * @param customerId id of the customer
     * @param paymentMethods list of payment methods
     * @throws UserNotFoundException if either of the user IDs (userId1 or userId2) is not found
     * @throws NotAuthorizedException if the user initiating the refund (userId1) is not authorized
     * @throws MissingBodyException if the request body is missing or invalid
     * @throws InvalidFieldException if any field in the request body is invalid
     */
    public void updateCustomerPaymentById(Long issuerId, Long customerId, List<PaymentMethod> paymentMethods)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {

        if (paymentMethods == null) {
            throw new MissingBodyException();
        }

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationData(customerId,
                issuerId, userRepository, new AuthorizationUserAndAdminStrategy())));

        Optional<Customer> optCustomer = customerRepository.findById(customerId);

        if (optCustomer.isEmpty()) {
            throw new UserNotFoundException(customerId);
        }

        Customer customer = optCustomer.get();
        customer.setPaymentMethods(paymentMethods);
        customerRepository.saveAndFlush(customer);

    }
}

