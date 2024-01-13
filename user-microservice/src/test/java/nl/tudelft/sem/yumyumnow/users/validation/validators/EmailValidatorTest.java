package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class EmailValidatorTest {
    EmailValidator emailValidator = new EmailValidator();
    ValidationObject validationObject;
    AuthorizationData authorizationData;
    UserRequestModel userRequestModel;

    @BeforeEach
    void setUp(){
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        userRequestModel = new UserRequestModel("email@s", "name", "surname");
        validationObject = new ValidationObject(userRequestModel, authorizationData);
    }

    @Test
    void testNameValidatorSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        try{
            emailValidator.handle(validationObject);
        } catch (Exception e){
            fail("exception should not be thrown");
        }
    }
    @Test
    void testEmailValidatorNull()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        userRequestModel.setEmail(null);
        assertThatThrownBy(() -> emailValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'email' with value 'null' is invalid");
    }
    @Test
    void testEmailValidatorEmpty()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        userRequestModel.setEmail("");
        assertThatThrownBy(() -> emailValidator
            .handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'email' with value '' is invalid");
    }
    @Test
    void testEmailValidatorBadEmail()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        userRequestModel.setEmail("email@");
        assertThatThrownBy(() -> emailValidator
            .handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'email' with value 'email@' is invalid");
    }
}
