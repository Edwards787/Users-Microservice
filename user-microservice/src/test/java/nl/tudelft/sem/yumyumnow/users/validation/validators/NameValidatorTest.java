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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class NameValidatorTest {
    transient NameValidator nameValidator = spy(new NameValidator());
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
    void testNameValidatorSuccess()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        boolean result = nameValidator.handle(validationObject);
        assertTrue(result);
        verify(nameValidator).checkNext(validationObject);
    }

    @Test
    void testNameValidatorSuccessNextFail()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        doReturn(false).when(nameValidator).checkNext(validationObject);
        boolean result = nameValidator.handle(validationObject);
        assertFalse(result);
        verify(nameValidator).checkNext(validationObject);
    }

    @Test
    void testNameValidatorNull()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        userRequestModel.setName(null);
        assertThatThrownBy(() -> nameValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'name' with value 'null' is invalid");
    }
    @Test
    void testNameValidatorEmpty()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        userRequestModel.setName("");
        assertThatThrownBy(() -> nameValidator
            .handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'name' with value '' is invalid");
    }
}
