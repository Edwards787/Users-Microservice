package nl.tudelft.sem.yumyumnow.users.services;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.yumyumnow.users.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Rating;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.CourierRepository;
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
public class CourierService {
    private final transient CourierRepository courierRepository;
    private final transient UserRepository userRepository;

    private final transient CourierBuilder courierBuilder;
    private final transient ValidatorConstructor validatorConstructor;
    private final transient HttpService httpService;


    /**
     * Constructor.
     *
     * @param courierRepository courier repository
     * @param userRepository    user repository
     * @param courierBuilder    courier builder
     */
    @Autowired
    public CourierService(
            CourierRepository courierRepository,
            UserRepository userRepository,
            CourierBuilder courierBuilder,
            HttpService httpService) {
        this.courierRepository = courierRepository;
        this.userRepository = userRepository;
        this.courierBuilder = courierBuilder;
        this.validatorConstructor = new ValidatorConstructor();
        this.httpService = httpService;
    }

    /**
     * Service that handles the retrieval of a courier by id.
     *
     * @return courier object
     */
    public Courier getCourierById(Long courierId) throws UserNotFoundException {
        Optional<Courier> courier = courierRepository.findById(courierId);

        if (courier.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            return courier.get();
        }
    }

    /**
     * Service that handles the creation of a courier.
     *
     * @return courier object
     */
    public Courier createCourier(@NotNull CourierRequestModel courierRequestModel)
            throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException,
            UserNotFoundException, NotAuthorizedException {

        Validator validator = validatorConstructor.courierWithoutAuthorization();
        validator.handle(new ValidationObject(courierRequestModel, null));

        Courier courier = new Courier();
        courierBuilder.build(courier, courierRequestModel);

        if (courierRepository.findByEmail(courierRequestModel.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException(courierRequestModel.getEmail());
        }

        Courier response = courierRepository.saveAndFlush(courier);
        courier.setId(response.getId());

        return courier;
    }

    public List<Courier> getAllCouriers() {
        return courierRepository.findAll();
    }

    /**
     * Service that handles the deletion of a courier.
     *
     * @throws UserNotFoundException   throw exception in case user was not found
     * @throws NotAuthorizedException  throw exception in case user was not authorized
     * @throws MissingBodyException    throw exception in case a body was missing
     * @throws InvalidFieldException   throw exception in case invalid fields were provided
     */
    public void deleteCourier(Long userId1, Long userId2)
            throws NotAuthorizedException, UserNotFoundException, MissingBodyException,
            InvalidFieldException {

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null,
                new AuthorizationData(userId1, userId2, userRepository,
                    new AuthorizationUserAndAdminStrategy())));

        courierRepository.deleteById(userId1);
        courierRepository.flush();
    }

    /**
     * Service that handles the update of a courier.
     *
     * @throws UserNotFoundException   throw exception in case user was not found
     * @throws NotAuthorizedException  throw exception in case user was not authorized
     * @throws MissingBodyException    throw exception in case a body was missing
     * @throws InvalidFieldException   throw exception in case invalid fields were provided
     */
    public void updateCourier(Long userId1, Long userId2, CourierRequestModel courierRequestModel)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {

        Validator validator = validatorConstructor.courierWithAuthorization();
        validator.handle(new ValidationObject(courierRequestModel,
                new AuthorizationData(userId1, userId2, userRepository,
                    new AuthorizationUserAndAdminStrategy())));

        Courier courierInitial;
        courierInitial = getCourierById(userId1);
        Courier courier = courierBuilder.build(courierInitial, courierRequestModel);
        courierRepository.saveAndFlush(courier);

    }

    /**
     * Service that handles the retrieval of past orders for a courier.
     *
     * @return list of order ids
     */
    public List<Long> getPastOrderIdsForCourier(Long userId) throws UserNotFoundException {
        Optional<Courier> courier = courierRepository.findById(userId);
        if (courier.isEmpty()) {
            throw new UserNotFoundException();
        }
        return httpService.getPastOrderIdsForCourier("http://localhost:8083", userId);
    }

    /**
     * Service that handles the retrieval of ratings for a courier.
     *
     * @return list of ratings
     */
    public List<Rating> getRatingsForCourier(Long userId) throws UserNotFoundException {
        Optional<Courier> courier = courierRepository.findById(userId);
        if (courier.isEmpty()) {
            throw new UserNotFoundException();
        }
        return courier.get().getRatings();
    }

    /**
     * Service that handles the update of ratings for a courier.
     *
     * @throws UserNotFoundException   throw exception in case user was not found
     * @throws NotAuthorizedException  throw exception in case user was not authorized
     * @throws MissingBodyException    throw exception in case a body was missing
     * @throws InvalidFieldException   throw exception in case invalid fields were provided
     */
    public void updateRatingsForCourier(Long userId1, Long userId2, List<Rating> ratings)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {

        if (ratings == null) {
            throw new InvalidFieldException("ratings", "null");
        }

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null,
                new AuthorizationData(userId1, userId2, userRepository,
                    new AuthorizationAdminOnlyStrategy())));

        Courier courier = getCourierById(userId1);
        courier.setRatings(ratings);
        courierRepository.saveAndFlush(courier);
    }


    /**
     * Post a rating to a courier. Rating can only be given by customers
     *
     * @param userId id of courier to be rated
     * @param rating rating
     * @throws UserNotFoundException if user not found
     */
    public void rateCourier(Long userId, Rating rating) throws UserNotFoundException,
            InvalidFieldException, MissingBodyException {
        if (rating == null) {
            throw new MissingBodyException();
        }

        // Check if customer exists
        if (userRepository.findById(rating.getUserId())
                .map(user -> !user.getRole().equals(User.RoleEnum.CUSTOMER))
                .orElse(true)) {
            throw new UserNotFoundException();
        }

        // Check if courier exists
        Optional<Courier> courier = courierRepository.findById(userId);
        if (courier.isEmpty()) {
            throw new UserNotFoundException();
        }

        // Check if rating value is valid
        if (rating.getRating() < 1 || rating.getRating() > 5) {
            throw new InvalidFieldException("rating", rating.getRating().toString());
        }

        courier.get().getRatings().add(rating);
        courierRepository.saveAndFlush(courier.get());
    }

    /**
     * Service that handles the update of activation status for a courier.
     *
     * @throws UserNotFoundException   throw exception in case user was not found
     * @throws NotAuthorizedException  throw exception in case user was not authorized
     * @throws MissingBodyException    throw exception in case a body was missing
     * @throws InvalidFieldException   throw exception in case invalid fields were provided
     */
    public void updateCourierActivationStatus(Long userId1, Long userId2, Boolean body)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {

        if (body == null) {
            throw new InvalidFieldException("activation status", "null");
        }

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null,
                new AuthorizationData(userId1, userId2, userRepository, new AuthorizationAdminOnlyStrategy())));

        Courier courier = getCourierById(userId1);
        courier.setActivated(body);
        courierRepository.saveAndFlush(courier);
    }

    /**
     * Service that handles the update of block status for a courier.
     *
     * @throws UserNotFoundException   throw exception in case user was not found
     * @throws NotAuthorizedException  throw exception in case user was not authorized
     * @throws MissingBodyException    throw exception in case a body was missing
     * @throws InvalidFieldException   throw exception in case invalid fields were provided
     */
    public void updateCourierBlockStatus(Long userId1, Long userId2, Boolean body)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {

        if (body == null) {
            throw new InvalidFieldException("block status", "null");
        }

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null,
                new AuthorizationData(userId1, userId2, userRepository,
                    new AuthorizationAdminOnlyStrategy())));

        Courier courier = getCourierById(userId1);
        courier.setBlocked(body);
        courierRepository.saveAndFlush(courier);
    }
}
