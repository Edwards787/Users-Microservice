package nl.tudelft.sem.yumyumnow.users.validation.Validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.Authorization.AuthorizationObject;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.ValidationObject;

import java.util.Optional;

public class AuthorizationValidator extends BaseValidator{

    @Override
    public boolean handle(ValidationObject validationObject)
        throws InvalidFieldException, NotAuthorizedException, MissingBodyException,
        UserNotFoundException {

        AuthorizationObject authorizationObject = validationObject.getAuthorizationObject();
        Long id1 = authorizationObject.getId1();
        Long id2 = authorizationObject.getId2();
        UserRepository userRepository = authorizationObject.getUserRepository();
        Accessibility strategy = authorizationObject.getAccessibility();

        checkExists(id1, id2, userRepository);

        switch (strategy) {
            case ADMIN_ONLY -> authorizeOnlyAdmin(id2, userRepository);
            case USER_AND_ADMIN -> authorizeUserAndAdmin(id1, id2, userRepository);
        }

        return super.checkNext(validationObject);
    }

    public void checkExists(Long target, Long request, UserRepository userRepository)
        throws UserNotFoundException {
        Optional<User> targetUser = userRepository.findById(target);
        Optional<User> requestUser = userRepository.findById(request);

        if (targetUser.isEmpty()) {
            throw new UserNotFoundException(target);
        }
        if (requestUser.isEmpty()) {
            throw new UserNotFoundException(request);
        }
    }
    public void authorizeOnlyAdmin(Long requester, UserRepository userRepository)
        throws UserNotFoundException, NotAuthorizedException {
        User requesterUser = userRepository.findById(requester).get();
        if (!requesterUser.getRole().equals(User.RoleEnum.ADMIN)) {
            throw new NotAuthorizedException();
        }
    }
    public void authorizeUserAndAdmin(Long target, Long requester, UserRepository userRepository)
        throws UserNotFoundException, NotAuthorizedException {
        Optional<User> targetUser = userRepository.findById(target);

        if (target.equals(requester)) return;
        else authorizeOnlyAdmin(requester, userRepository);
    }
}
