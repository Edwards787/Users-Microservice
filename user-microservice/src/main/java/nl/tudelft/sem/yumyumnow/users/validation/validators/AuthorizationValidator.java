package nl.tudelft.sem.yumyumnow.users.validation.validators;

import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.Accessibility;
import nl.tudelft.sem.yumyumnow.users.validation.objects.AuthorizationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;


public class AuthorizationValidator extends BaseValidator {

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
            default -> throw new RuntimeException();
        }

        return super.checkNext(validationObject);
    }

    /**
     * Checks whether the target and requester exist.
     *
     * @param target         target id
     * @param request        requester id
     * @param userRepository user repository
     * @throws UserNotFoundException if the target or requester does not exist
     */
    public void checkExists(Long target, Long request, UserRepository userRepository)
            throws UserNotFoundException {
        Optional<User> targetUser = userRepository.findById(target);
        if (targetUser.isEmpty()) {
            throw new UserNotFoundException(target);
        }
        Optional<User> requestUser = userRepository.findById(request);
        if (requestUser.isEmpty()) {
            throw new UserNotFoundException(request);
        }
    }

    /**
     * Checks whether the requester is an admin.
     */
    public void authorizeOnlyAdmin(Long requester, UserRepository userRepository)
            throws UserNotFoundException, NotAuthorizedException {
        User requesterUser = userRepository.findById(requester).get();
        if (!requesterUser.getRole().equals(User.RoleEnum.ADMIN)) {
            throw new NotAuthorizedException();
        }
    }

    /**
     * Checks whether the requester is the target.
     */
    public void authorizeUserAndAdmin(Long target, Long requester, UserRepository userRepository)
            throws UserNotFoundException, NotAuthorizedException {
        if (!target.equals(requester)) {
            authorizeOnlyAdmin(requester, userRepository);
        }
    }
}
