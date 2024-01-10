package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.services.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuthorizationServiceTest {
    private UserRepository userRepository;
    private AuthorizationService authorizationService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        authorizationService = new AuthorizationService(userRepository);
    }

    @Test
    void targetNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        try {
            authorizationService.authorize(1L, 2L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UserNotFoundException.class);
            assert e instanceof UserNotFoundException;
            assertThat(((UserNotFoundException) e).getTargetId()).isEqualTo(1L);
        }
    }

    @Test
    void requesterNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        try {
            authorizationService.authorize(1L, 2L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UserNotFoundException.class);
            assert e instanceof UserNotFoundException;
            assertThat(((UserNotFoundException) e).getTargetId()).isEqualTo(2L);
        }
    }

    @Test
    void requesterNotAuthorized() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User().role(User.RoleEnum.VENDOR)));
        try {
            authorizationService.authorize(1L, 2L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NotAuthorizedException.class);
        }
    }

    @Test
    void requesterAuthorizedSame() throws UserNotFoundException, NotAuthorizedException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        authorizationService.authorize(1L, 1L);
    }

    @Test
    void requesterAuthorizedAdmin() throws UserNotFoundException, NotAuthorizedException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User().role(User.RoleEnum.ADMIN)));
        authorizationService.authorize(1L, 2L);
    }
}
