package nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization;

import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestAuthorizationStrategy {

    UserRepository userRepository;
    AuthorizationStrategy strategy = new AuthorizationAdminOnlyStrategy();

    @BeforeEach
    void setUp() {
        User user = new User();

        userRepository = mock(UserRepository.class);
        when(userRepository.findById(0L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    void testCheckExistsFail() throws UserNotFoundException {
        assertThrows(UserNotFoundException.class, () -> {
            strategy.checkExists(0L, 1L, userRepository);
        });
    }
    @Test
    void testCheckExistsSuccess() throws UserNotFoundException {
        try{
            strategy.checkExists(1L, 1L, userRepository);
        }catch (Exception e){
            fail("should not throw exceptions");
        }
    }
}