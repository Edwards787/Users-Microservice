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
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

public class SurnameValidatorTest {
    transient SurnameValidator surnameValidator = new SurnameValidator();
    transient ValidationObject validationObject;
    transient AuthorizationData authorizationData;
    transient UserRequestModel userRequestModel;

    @BeforeEach
    void setUp(){
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        userRequestModel = new UserRequestModel("email", "name", "surname");
        validationObject = new ValidationObject(userRequestModel, authorizationData);
    }

    @Test
    void testSurnameValidatorSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        try{
            surnameValidator.handle(validationObject);
        } catch (Exception e){
            fail("exception should not be thrown");
        }
    }
    @Test
    void testSurnameValidatorNull()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        userRequestModel.setSurname(null);
        assertThatThrownBy(() -> surnameValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'surname' with value 'null' is invalid");
    }
    @Test
    void testSurnameValidatorEmpty()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        userRequestModel.setSurname("");
        assertThatThrownBy(() -> surnameValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'surname' with value '' is invalid");
    }
}
