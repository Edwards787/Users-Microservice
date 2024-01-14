package nl.tudelft.sem.yumyumnow.users.validation.objects.authorization;

import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;

public interface AuthorizationStrategy {

    void authorize(Long target, Long requester, UserRepository userRepository)
        throws NotAuthorizedException, UserNotFoundException;

    /**
     * Checks if both users are actually in the database.
     *
     * @param target                    target ID
     * @param request                   requester's ID
     * @param userRepository            user repo
     * @throws UserNotFoundException    if user is not found, throw exception
     */
    default void checkExists(Long target, Long request, UserRepository userRepository)
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
}
