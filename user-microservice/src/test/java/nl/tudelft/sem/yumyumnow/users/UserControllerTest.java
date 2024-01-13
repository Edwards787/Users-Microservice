package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.controllers.UserController;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private User user;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        user = new User();
        user.setId(1L);
        user.setName("user1");
        user.setEmail( "user1@example.com");
    }


    @Test
    void getAllUsers_Success() {
        List<User> userList = List.of(user);

        when(userService.getAllUsers()).thenReturn(userList);

        ResponseEntity<List<User>> responseEntity = userController.getAllUsers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userList, responseEntity.getBody());
    }

    @Test
    void getAllUsers_ExceptionThrown() {
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Test Exception"));

        assertThrows(ResponseStatusException.class, () -> userController.getAllUsers());
    }

    @Test
    void getUserByEmail_Success() throws UserNotFoundException, InvalidFieldException {
        String userEmail = "user1@example.com";

        when(userService.getUserByEmail(userEmail)).thenReturn(user);

        ResponseEntity<User> responseEntity = userController.getUserByEmail(userEmail);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user, responseEntity.getBody());
    }

    @Test
    void getUserByEmail_UserNotFoundException() throws UserNotFoundException,
        InvalidFieldException {
        String userEmail = "nonexistent@example.com";

        when(userService.getUserByEmail(userEmail)).thenThrow(new UserNotFoundException());
        assertThrows(ResponseStatusException.class, () -> userController.getUserByEmail(userEmail));
    }

    @Test
    void getUserByEmail_ExceptionThrown() throws UserNotFoundException, InvalidFieldException {
        String userEmail = "user1@example.com";
        when(userService.getUserByEmail(userEmail)).thenThrow(new UserNotFoundException());

        assertThrows(ResponseStatusException.class, () -> userController.getUserByEmail(userEmail));
    }

    @Test
    void getUserById_Success() throws UserNotFoundException {
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(user);
        ResponseEntity<User> responseEntity = userController.getUserById(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user, responseEntity.getBody());
    }

    @Test
    void getUserById_UserNotFoundException() throws UserNotFoundException {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException());

        assertThrows(ResponseStatusException.class, () -> userController.getUserById(userId));
    }

    @Test
    void getUserById_ExceptionThrown() throws UserNotFoundException {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException());

        assertThrows(ResponseStatusException.class, () -> userController.getUserById(userId));
    }

    @Test
    void getAllUsers_MockMvc_Success() throws Exception {
        List<User> userList = List.of(user);

        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserByEmail_MockMvc_Success() throws Exception {
        String userEmail = user.getEmail();

        when(userService.getUserByEmail(userEmail)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/user/byEmail/{email}", userEmail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
