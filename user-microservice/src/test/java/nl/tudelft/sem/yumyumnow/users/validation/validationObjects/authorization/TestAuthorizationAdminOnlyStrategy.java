package nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization;

import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAuthorizationAdminOnlyStrategy {

    UserRepository userRepository;
    AuthorizationAdminOnlyStrategy strategy;
    @BeforeEach
    void setUp(){
        User user = new User();
        user.setRole(User.RoleEnum.VENDOR);

        User admin = new User();
        admin.setRole(User.RoleEnum.ADMIN);

        userRepository = mock(UserRepository.class);
        when(userRepository.findById(0L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        strategy = new AuthorizationAdminOnlyStrategy();
    }

    @Test
    void testAuthorizeSuccess(){
        try {
            strategy.authorize(1L, 2L, userRepository);
        } catch(Exception e) {
            fail("Should not have thrown any exception");
        }
    }
    @Test
    void testAuthorizeNotFound(){
        assertThrows(UserNotFoundException.class, () -> {
            strategy.authorize(0L, 2L, userRepository);
        });
    }
    @Test
    void testAuthorizeNotAuthorized(){
        assertThrows(NotAuthorizedException.class, () -> {
            strategy.authorize(1L, 1L, userRepository);
        });
    }
}
