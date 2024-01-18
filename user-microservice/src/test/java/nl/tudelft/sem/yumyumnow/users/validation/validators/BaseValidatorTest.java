package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseValidatorTest {
    transient BaseValidator validator;
    @BeforeEach
    void setUp(){
        validator = new IbanValidator();
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
        validator.setNext(new IbanValidator());
        assertThat(validator.getNext()).isInstanceOf(IbanValidator.class);
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
        IbanValidator ibanValidator = mock(IbanValidator.class);
        ValidationObject validationObject = new ValidationObject(null, null);
        when(ibanValidator.handle(validationObject)).thenReturn(false);

        validator.setNext(ibanValidator);
        assertThat(validator.checkNext(validationObject)).isFalse();
    }

    @Test
    void testTrue() throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        IbanValidator ibanValidator = mock(IbanValidator.class);
        ValidationObject validationObject = new ValidationObject(null, null);
        when(ibanValidator.handle(validationObject)).thenReturn(true);

        validator.setNext(ibanValidator);
        assertThat(validator.checkNext(validationObject)).isTrue();
    }
}
