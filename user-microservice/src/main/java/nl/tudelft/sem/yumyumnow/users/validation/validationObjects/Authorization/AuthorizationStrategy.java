package nl.tudelft.sem.yumyumnow.users.validation.validationObjects.Authorization;

import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;

import java.util.Optional;

public interface AuthorizationStrategy {

    void authorize(Long target, Long requester, UserRepository userRepository)
        throws NotAuthorizedException, UserNotFoundException;
    default void checkExists(Long target, Long request, UserRepository userRepository)
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
}
