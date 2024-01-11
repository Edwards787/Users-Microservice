package nl.tudelft.sem.yumyumnow.users.services;
import nl.tudelft.sem.yumyumnow.users.builders.CourierBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.CourierValidator;
import nl.tudelft.sem.yumyumnow.users.repositories.CourierRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.Objects.Accessibility;
import nl.tudelft.sem.yumyumnow.users.validation.Objects.AuthorizationObject;
import nl.tudelft.sem.yumyumnow.users.validation.Objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.Objects.Validators;
import nl.tudelft.sem.yumyumnow.users.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class CourierService {
    private final CourierRepository courierRepository;
    private final UserRepository userRepository;

    private final CourierBuilder courierBuilder;
    private final Validators validators;
    @Autowired
    public CourierService(CourierRepository courierRepository, UserRepository userRepository, CourierBuilder courierBuilder){
        this.courierRepository = courierRepository;
        this.userRepository = userRepository;
        this.courierBuilder = courierBuilder;
        this.validators = new Validators();
    }

    public Courier getCourierById(Long courierId) throws UserNotFoundException{
        Optional<Courier> courier = courierRepository.findById(courierId);

        if(courier.isEmpty()) throw new UserNotFoundException();
        else return courier.get();
    }

    public Courier createCourier(@NotNull CourierRequestModel courierRequestModel)
        throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {

        Validator validator = validators.courierWithoutAuthorization();
        validator.handle(new ValidationObject(courierRequestModel, null));

        Courier dummy = new Courier();
        Courier courier = courierBuilder.build(dummy, courierRequestModel);

        if (courierRepository.findByEmail(courierRequestModel.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException(courierRequestModel.getEmail());
        }

        Courier response = courierRepository.saveAndFlush(courier);
        courier.setId(response.getId());

        return courier;
    }

    public List<Courier> getAllCouriers(){
        return courierRepository.findAll();
    }

    public void deleteCourier(Long userId1, Long userId2)
        throws NotAuthorizedException, UserNotFoundException, MissingBodyException,
        InvalidFieldException {
        Courier courier;
        courier = getCourierById(userId1);

        Validator validator = validators.userAuthorization();
        validator.handle(new ValidationObject(null,
            new AuthorizationObject(userId1, userId2, userRepository,
                Accessibility.USER_AND_ADMIN)));

        courierRepository.deleteById(userId1);
        courierRepository.flush();
    }

    public void updateCourier(Long userId1, Long userId2, CourierRequestModel courierRequestModel)
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        Validator validator = validators.courierWithAuthorization();
        validator.handle(new ValidationObject(courierRequestModel,
            new AuthorizationObject(userId1, userId2, userRepository,
                Accessibility.USER_AND_ADMIN)));

        Courier courierInitial;
        courierInitial = getCourierById(userId1);
        Courier courier = courierBuilder.build(courierInitial, courierRequestModel);
        courierRepository.saveAndFlush(courier);
    }

    public List<Long> getPastOrderIdsForCourier(Long userId) throws UserNotFoundException {
        Optional<Courier> courier = courierRepository.findById(userId);
        if(courier.isEmpty()) throw new UserNotFoundException();
        //TODO : MAKE SURE TO IMPLEMENT THIS WHEN WE AGREE ON HOW TO GET THEM
        else return List.of(1L, 2L, 3L);
    }

    public List<Rating> getRatingsForCourier(Long userId) throws UserNotFoundException {
        Optional<Courier> courier = courierRepository.findById(userId);
        if(courier.isEmpty()) throw new UserNotFoundException();
        else return courier.get().getRatings();
    }

    public void updateRatingsForCourier(Long userId1, Long userId2, List<Rating> ratings)
            throws UserNotFoundException, NotAuthorizedException {
        //TODO TEST
        Courier courier;
        courier = getCourierById(userId1);

        Optional<User> admin = userRepository.findById(userId2);
        if(admin.isEmpty()){
            throw new NotAuthorizedException();
        }
        User usr = admin.get();
        if(usr.getRole().equals(User.RoleEnum.ADMIN)){
            courier.setRatings(ratings);
            courierRepository.saveAndFlush(courier);
            return;
        }
        else throw new NotAuthorizedException();
    }

    public void updateCourierActivationStatus(Long userId1, Long userId2, Boolean body)
            throws UserNotFoundException, NotAuthorizedException {

        Courier courier;
        courier = getCourierById(userId1);

        Optional<User> user = userRepository.findById(userId2);
        if(user.isEmpty()) throw new NotAuthorizedException();
        User admin = user.get();
        if(admin.getRole().equals(User.RoleEnum.ADMIN)){
            courier.setActivated(body);
            courierRepository.saveAndFlush(courier);
            return;
        }
        else throw new NotAuthorizedException();
    }

    public void updateCourierBlockStatus(Long userId1, Long userId2, Boolean body)
            throws UserNotFoundException, NotAuthorizedException {

        Courier courier;
        courier = getCourierById(userId1);

        Optional<User> user = userRepository.findById(userId2);
        if(user.isEmpty()) throw new NotAuthorizedException();
        User admin = user.get();
        if(admin.getRole().equals(User.RoleEnum.ADMIN)){
            //if here then an admin is issuing on an existing Courier
            courier.setBlocked(body);
            courierRepository.saveAndFlush(courier);
            return;
        }
        else throw new NotAuthorizedException();
    }
}
