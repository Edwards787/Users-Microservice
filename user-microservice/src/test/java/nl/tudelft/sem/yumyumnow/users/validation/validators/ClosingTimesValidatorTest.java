package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ClosingTimesValidatorTest {
    transient ClosingTimesValidator closingTimesValidator = spy(new ClosingTimesValidator());
    transient ValidationObject validationObject;
    transient AuthorizationData authorizationData;
    transient VendorRequestModel vendorRequestModel;

    @BeforeEach
    void setUp(){
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        vendorRequestModel = new VendorRequestModel(new Address(), "a", "a", "email", "name", "surname");
        validationObject = new ValidationObject(vendorRequestModel, authorizationData);
    }

    @Test
    void testClosingTimesValidatorSuccess()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        vendorRequestModel.setClosingTimes("1");
        boolean result = closingTimesValidator.handle(validationObject);
        assertTrue(result);
        verify(closingTimesValidator).checkNext(validationObject);
    }

    @Test
    void testClosingTimesValidatorSuccessNextFail()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        vendorRequestModel.setClosingTimes("1");
        doReturn(false).when(closingTimesValidator).checkNext(validationObject);
        boolean result = closingTimesValidator.handle(validationObject);
        assertFalse(result);
        verify(closingTimesValidator).checkNext(validationObject);
    }

    @Test
    void testClosingTimesValidatorNull()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        vendorRequestModel.setClosingTimes(null);
        assertThatThrownBy(() -> closingTimesValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'closing times' with value 'null' is invalid");
    }
    @Test
    void testClosingTimesValidatorEmpty()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        vendorRequestModel.setClosingTimes("");
        assertThatThrownBy(() -> closingTimesValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'closing times' with value '' is invalid");
    }
}
