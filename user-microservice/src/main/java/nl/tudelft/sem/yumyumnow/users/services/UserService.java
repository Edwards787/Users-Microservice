package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) throws UserNotFoundException, InvalidFieldException {
        if (email == null) throw new InvalidFieldException("email", "null");

        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) throw new UserNotFoundException();
        return user.get();
    }

    public User getUserById(Long id) throws UserNotFoundException{
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) throw new UserNotFoundException();
        return user.get();
    }
}
