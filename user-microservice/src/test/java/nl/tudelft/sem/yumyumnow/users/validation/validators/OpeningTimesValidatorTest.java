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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class OpeningTimesValidatorTest {
    transient OpeningTimesValidator openingTimesValidator = spy(new OpeningTimesValidator());
    transient ValidationObject validationObject;
    transient AuthorizationData authorizationData;
    transient VendorRequestModel vendorRequestModel;

    @BeforeEach
    void setUp(){
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        vendorRequestModel = new VendorRequestModel(new Address(), "time", "time", "email", "name", "surname");
        validationObject = new ValidationObject(vendorRequestModel, authorizationData);
    }

    @Test
    void testOpeningTimesValidatorSuccess()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        vendorRequestModel.setOpeningTimes("1");
        boolean result = openingTimesValidator.handle(validationObject);
        assertTrue(result);
        verify(openingTimesValidator).checkNext(validationObject);
    }

    @Test
    void testOpeningTimesValidatorSuccessNextFail()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        vendorRequestModel.setOpeningTimes("1");
        doReturn(false).when(openingTimesValidator).checkNext(validationObject);
        boolean result = openingTimesValidator.handle(validationObject);
        assertFalse(result);
        verify(openingTimesValidator).checkNext(validationObject);
    }

    @Test
    void testOpeningTimesValidatorNull()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        vendorRequestModel.setOpeningTimes(null);
        assertThatThrownBy(() -> openingTimesValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'opening times' with value 'null' is invalid");
    }
    @Test
    void testOpeningTimesValidatorEmpty()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        vendorRequestModel.setOpeningTimes("");
        assertThatThrownBy(() -> openingTimesValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'opening times' with value '' is invalid");
    }
}
