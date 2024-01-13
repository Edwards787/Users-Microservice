package nl.tudelft.sem.yumyumnow.users.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final transient UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Service that handles the retrieval of a user by email.
     *
     * @return user object
     */
    public User getUserByEmail(String email) throws UserNotFoundException, InvalidFieldException {
        if (email == null) {
            throw new InvalidFieldException("email", "null");
        }

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        return user.get();
    }

    /**
     * Service that handles the retrieval of a user by id.
     *
     * @return user object
     */
    public User getUserById(Long id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        return user.get();
    }
}
