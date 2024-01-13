package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationData;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationUserAndAdminStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthorizationValidatorTest {

    AuthorizationValidator authorizationValidator = new AuthorizationValidator();
    ValidationObject validationObject;
    AuthorizationData authorizationData;
    UserRepository userRepository;

    @BeforeEach
    void setUp(){
        User user1 = new User();
        user1.setRole(User.RoleEnum.ADMIN);
        User user2 = new User();
        user2.setRole(User.RoleEnum.VENDOR);

        userRepository = mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
    }

    @Test
    void testSuccessAdminOnly(){
        authorizationData = new AuthorizationData(2L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        validationObject = new ValidationObject(null, authorizationData);

        try{
            authorizationValidator.handle(validationObject);
        } catch (Exception e){
            fail("Shoudl not throw exceptions");
        }
    }
    @Test
    void testSuccessAdminAndUser(){
        authorizationData = new AuthorizationData(2L, 2L, userRepository, new AuthorizationUserAndAdminStrategy());
        validationObject = new ValidationObject(null, authorizationData);

        try{
            authorizationValidator.handle(validationObject);
        } catch (Exception e){
            fail("Not authorized");
        }
    }
    @Test
    void testUserNotFoundFirst(){
        authorizationData = new AuthorizationData(2L, 3L, userRepository, new AuthorizationAdminOnlyStrategy());
        validationObject = new ValidationObject(null, authorizationData);

        assertThatThrownBy(() -> authorizationValidator.handle(validationObject))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("User not found");
    }
    @Test
    void testUserNotFoundSecond(){
        authorizationData = new AuthorizationData(3L, 2L, userRepository, new AuthorizationAdminOnlyStrategy());
        validationObject = new ValidationObject(null, authorizationData);

        assertThatThrownBy(() -> authorizationValidator.handle(validationObject))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("User not found");
    }
    @Test
    void testUnauthorizedAdminOnly(){
        authorizationData = new AuthorizationData(1L, 2L, userRepository, new AuthorizationAdminOnlyStrategy());
        validationObject = new ValidationObject(null, authorizationData);

        assertThatThrownBy(() -> authorizationValidator.handle(validationObject))
            .isInstanceOf(NotAuthorizedException.class)
            .hasMessage("Not authorized");
    }
    @Test
    void testUnauthorizedUserAndAdmin(){
        Admin admin = new Admin();
        admin.setRole(User.RoleEnum.VENDOR);
        when(userRepository.findById(3L)).thenReturn(Optional.of(admin));

        authorizationData = new AuthorizationData(2L, 3L, userRepository, new AuthorizationUserAndAdminStrategy());
        validationObject = new ValidationObject(null, authorizationData);

        assertThatThrownBy(() -> authorizationValidator.handle(validationObject))
            .isInstanceOf(NotAuthorizedException.class)
            .hasMessage("Not authorized");
    }
}
