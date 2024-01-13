package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;

public class AllergenValidatorTest {
    AllergenValidator allergenValidator = new AllergenValidator();
    ValidationObject validationObject;
    AuthorizationData authorizationData;
    CustomerRequestModel customerRequestModel;

    @BeforeEach
    void setUp(){
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        customerRequestModel = new CustomerRequestModel(new Address(), new ArrayList<>(),"email", "name", "surname");
        validationObject = new ValidationObject(customerRequestModel, authorizationData);
    }

    @Test
    void testAllergenValidatorSuccess()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        customerRequestModel.setAllergens(new ArrayList<>());
        try{
            allergenValidator.handle(validationObject);
        } catch (Exception e){
            fail("exception should not be thrown");
        }
    }
    @Test
    void testAllergenValidatorNull()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        customerRequestModel.setAllergens(null);
        assertThatThrownBy(() -> allergenValidator.handle(validationObject))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'allergens' with value 'null' is invalid");
    }
}
