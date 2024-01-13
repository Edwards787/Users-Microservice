package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class IbanValidatorTest {
    IbanValidator ibanValidator = new IbanValidator();
    ValidationObject validationObject;
    AuthorizationData authorizationData;
    CourierRequestModel courierRequestModel;

    @BeforeEach
    void setUp(){
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        courierRequestModel = new CourierRequestModel("email", "name", "surname");
        validationObject = new ValidationObject(courierRequestModel, authorizationData);
    }

    @Test
    void testIBANValidatorSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        try{
            courierRequestModel.setIbanNumber("iban");
            ibanValidator.handle(validationObject);
        } catch (Exception e){
            fail("exception should not be thrown");
        }
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
