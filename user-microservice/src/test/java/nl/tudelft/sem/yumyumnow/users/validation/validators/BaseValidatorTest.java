package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.ValidationObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseValidatorTest {

    BaseValidator validator;
    @BeforeEach
    void setUp(){
        validator = new IBANValidator();
        validator.setNext(new EmailValidator());
    }

    @Test
    void testGetNext()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        assertThat(validator.getNext()).isInstanceOf(EmailValidator.class);
    }
    @Test
    void testSetNext(){
        validator.setNext(new IBANValidator());
        assertThat(validator.getNext()).isInstanceOf(IBANValidator.class);
    }

    @Test
    void testNull() throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        validator.setNext(null);
        assertThat(validator.checkNext(null)).isEqualTo(true);
    }

    @Test
    void testFalse() throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        IBANValidator ibanValidator = mock(IBANValidator.class);
        ValidationObject validationObject = new ValidationObject(null, null);
        when(ibanValidator.handle(validationObject)).thenReturn(false);

        validator.setNext(ibanValidator);
        assertThat(validator.checkNext(validationObject)).isEqualTo(false);
    }
}
