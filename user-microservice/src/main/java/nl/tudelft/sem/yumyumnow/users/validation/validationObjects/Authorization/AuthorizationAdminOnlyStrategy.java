package nl.tudelft.sem.yumyumnow.users.validation.validationObjects.Authorization;

import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;

public class AuthorizationAdminOnlyStrategy implements AuthorizationStrategy{

    @Override
    public void authorize(Long target, Long requester, UserRepository userRepository)
        throws NotAuthorizedException, UserNotFoundException {
        checkExists(target, requester, userRepository);

        User requesterUser = userRepository.findById(requester).get();
        if (!requesterUser.getRole().equals(User.RoleEnum.ADMIN)) {
            throw new NotAuthorizedException();
        }
    }
}
