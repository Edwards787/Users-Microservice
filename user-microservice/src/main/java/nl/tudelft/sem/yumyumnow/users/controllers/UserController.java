package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.UserApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class UserController implements UserApi {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users;
        try{
            users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Something went wrong");
        }
    }

    @Override
    public ResponseEntity<User> getUserByEmail(String email){
        User user;
        try{
            user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        }
        catch (UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with given email not found.");
        }
    }

    @Override
    public ResponseEntity<User> getUserById(Long id){
        User user;
        try{
            user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        }
        catch(UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.");
        }
    }
}
