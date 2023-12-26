package nl.tudelft.sem.yumyumnow.users.services;

import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthorizationService {
    private final UserRepository userRepository;

    @Autowired
    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authorizes a request.
     *
     * @param target the target user
     * @param requester the requester user
     * @throws UserNotFoundException if the target or requester user is not found
     * @throws NotAuthorizedException if the requester is not authorized to perform the request
     */
    public void authorize(Long target, Long requester) throws UserNotFoundException, NotAuthorizedException {
        Optional<User> targetUser = userRepository.findById(target);
        if (targetUser.isEmpty()) {
            throw new UserNotFoundException(target);
        }
        if (!target.equals(requester)) {
            Optional<User> requesterUser = userRepository.findById(requester);
            if (requesterUser.isEmpty()) {
                throw new UserNotFoundException(requester);
            }
            if (!requesterUser.get().getRole().equals(User.RoleEnum.ADMIN)) {
                throw new NotAuthorizedException();
            }
        }
    }
}
