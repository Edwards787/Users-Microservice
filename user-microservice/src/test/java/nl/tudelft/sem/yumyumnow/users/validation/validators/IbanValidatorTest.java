package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
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

public class IbanValidatorTest {
    transient IbanValidator ibanValidator = spy(new IbanValidator());
    transient ValidationObject validationObject;
    transient AuthorizationData authorizationData;
    transient CourierRequestModel courierRequestModel;

    @BeforeEach
    void setUp() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        courierRequestModel = new CourierRequestModel("email", "name", "surname");
        validationObject = new ValidationObject(courierRequestModel, authorizationData);
    }

    @Test
    void testIBANValidatorSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        courierRequestModel.setIbanNumber("iban");
        boolean result = ibanValidator.handle(validationObject);
        assertTrue(result);
        verify(ibanValidator).checkNext(validationObject);
    }

    @Test
    void testIBANValidatorSuccessNextFail()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        courierRequestModel.setIbanNumber("iban");
        doReturn(false).when(ibanValidator).checkNext(validationObject);
        boolean result = ibanValidator.handle(validationObject);
        assertFalse(result);
        verify(ibanValidator).checkNext(validationObject);
    }

    @Test
    void testIBANValidatorNull()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        courierRequestModel.setIbanNumber(null);
        assertThatThrownBy(() -> ibanValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'iban' with value 'null' is invalid");
    }
}
