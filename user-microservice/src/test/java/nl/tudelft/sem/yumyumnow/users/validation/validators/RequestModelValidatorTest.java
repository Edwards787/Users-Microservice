package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class RequestModelValidatorTest {
    transient RequestModelValidator requestModelValidator = spy(new RequestModelValidator());

    transient ValidationObject validationObject;
    transient UserRequestModel userRequestModel;

    @BeforeEach
    void setUp() {
        userRequestModel = new UserRequestModel("email@email.com", "Joe", "Bidem");
        validationObject = new ValidationObject(userRequestModel, null);
    }

    @Test
    void testOpeningTimesValidatorSuccess()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        boolean result = requestModelValidator.handle(validationObject);
        assertTrue(result);
        verify(requestModelValidator).checkNext(validationObject);
    }

    @Test
    void testOpeningTimesValidatorSuccessNextFail()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        doReturn(false).when(requestModelValidator).checkNext(validationObject);
        boolean result = requestModelValidator.handle(validationObject);
        assertFalse(result);
        verify(requestModelValidator).checkNext(validationObject);
    }

    @Test
    void testRequestModelNull()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        ValidationObject validationObject1 = new ValidationObject(null, null);
        assertThatThrownBy(() -> requestModelValidator.handle(validationObject1))
            .isInstanceOf(MissingBodyException.class)
            .hasMessage("Body is missing");
    }
}
