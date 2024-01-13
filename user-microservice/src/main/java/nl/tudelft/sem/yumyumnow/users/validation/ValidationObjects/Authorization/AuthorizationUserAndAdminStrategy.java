package nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.Authorization;

import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;

import java.util.Optional;

public class AuthorizationUserAndAdminStrategy implements AuthorizationStrategy{

    @Override
    public void authorize(AuthorizationObject authorizationObject)
        throws NotAuthorizedException, UserNotFoundException {
        UserRepository userRepository = authorizationObject.getUserRepository();
        Long target = authorizationObject.getId1();
        Long requester = authorizationObject.getId2();

        checkExists(target, requester, userRepository);
        if (target.equals(requester)) return;

        User requesterUser = userRepository.findById(requester).get();
        if (!requesterUser.getRole().equals(User.RoleEnum.ADMIN)) {
            throw new NotAuthorizedException();
        }
    }
}
