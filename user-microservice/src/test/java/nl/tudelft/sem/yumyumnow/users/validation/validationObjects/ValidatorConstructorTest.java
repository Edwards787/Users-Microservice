package nl.tudelft.sem.yumyumnow.users.validation.validationObjects;

import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationData;
import nl.tudelft.sem.yumyumnow.users.validation.validators.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class ValidatorConstructorTest {
    ValidatorConstructor validatorConstructor = new ValidatorConstructor();
    ValidationObject validationObject;
    AuthorizationData authorizationData;
    VendorRequestModel vendorRequestModel;

    @BeforeEach
    void setUp(){
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        vendorRequestModel = new VendorRequestModel(new Address(), "a", "a", "email", "name", "surname");
        validationObject = new ValidationObject(vendorRequestModel, authorizationData);
    }
    @Test
    void testUserWithoutAuthorization(){
        assertThat(validatorConstructor.userWithoutAuthorization()).isInstanceOf(RequestModelValidator.class);

        RequestModelValidator requestModelValidator = (RequestModelValidator) validatorConstructor.userWithoutAuthorization();
        assertThat(requestModelValidator.getNext()).isInstanceOf(NameValidator.class);

        NameValidator nameValidator =  (NameValidator) requestModelValidator.getNext();
        assertThat(nameValidator.getNext()).isInstanceOf(SurnameValidator.class);

        SurnameValidator surnameValidator =  (SurnameValidator) nameValidator.getNext();
        assertThat(surnameValidator.getNext()).isInstanceOf(EmailValidator.class);

        EmailValidator emailValidator = (EmailValidator) surnameValidator.getNext();
        assertThat(emailValidator.getNext()).isNull();
    }
    @Test
    void testUserWithAuthorization(){
        assertThat(validatorConstructor.userWithAuthorization()).isInstanceOf(AuthorizationValidator.class);

        AuthorizationValidator authorizationValidator = (AuthorizationValidator) validatorConstructor.userWithAuthorization();
        assertThat(authorizationValidator.getNext()).isInstanceOf(RequestModelValidator.class);

        RequestModelValidator requestModelValidator = (RequestModelValidator) authorizationValidator.getNext();
        assertThat(requestModelValidator.getNext()).isInstanceOf(NameValidator.class);

        NameValidator nameValidator =  (NameValidator) requestModelValidator.getNext();
        assertThat(nameValidator.getNext()).isInstanceOf(SurnameValidator.class);

        SurnameValidator surnameValidator =  (SurnameValidator) nameValidator.getNext();
        assertThat(surnameValidator.getNext()).isInstanceOf(EmailValidator.class);

        EmailValidator emailValidator = (EmailValidator) surnameValidator.getNext();
        assertThat(emailValidator.getNext()).isNull();
    }
    @Test
    void testUserAuthorization(){
        assertThat(validatorConstructor.userAuthorization()).isInstanceOf(AuthorizationValidator.class);
        AuthorizationValidator authorizationValidator = (AuthorizationValidator) validatorConstructor.userAuthorization();
        assertThat(authorizationValidator.getNext()).isNull();
    }

    @Test
    void testCourierWithoutAuthorization(){
        assertThat(validatorConstructor.courierWithoutAuthorization()).isInstanceOf(RequestModelValidator.class);

        RequestModelValidator requestModelValidator = (RequestModelValidator) validatorConstructor.courierWithoutAuthorization();
        assertThat(requestModelValidator.getNext()).isInstanceOf(NameValidator.class);

        NameValidator nameValidator =  (NameValidator) requestModelValidator.getNext();
        assertThat(nameValidator.getNext()).isInstanceOf(SurnameValidator.class);

        SurnameValidator surnameValidator =  (SurnameValidator) nameValidator.getNext();
        assertThat(surnameValidator.getNext()).isInstanceOf(EmailValidator.class);

        EmailValidator emailValidator = (EmailValidator) surnameValidator.getNext();
        assertThat(emailValidator.getNext()).isInstanceOf(IbanValidator.class);

        IbanValidator ibanValidator = (IbanValidator) emailValidator.getNext();
        assertThat(ibanValidator.getNext()).isNull();
    }
    @Test
    void testCourierWithAuthorization(){
        assertThat(validatorConstructor.courierWithAuthorization()).isInstanceOf(AuthorizationValidator.class);

        AuthorizationValidator authorizationValidator = (AuthorizationValidator) validatorConstructor.courierWithAuthorization();
        assertThat(authorizationValidator.getNext()).isInstanceOf(RequestModelValidator.class);

        RequestModelValidator requestModelValidator = (RequestModelValidator) authorizationValidator.getNext();
        assertThat(requestModelValidator.getNext()).isInstanceOf(NameValidator.class);

        NameValidator nameValidator =  (NameValidator) requestModelValidator.getNext();
        assertThat(nameValidator.getNext()).isInstanceOf(SurnameValidator.class);

        SurnameValidator surnameValidator =  (SurnameValidator) nameValidator.getNext();
        assertThat(surnameValidator.getNext()).isInstanceOf(EmailValidator.class);

        EmailValidator emailValidator = (EmailValidator) surnameValidator.getNext();
        assertThat(emailValidator.getNext()).isInstanceOf(IbanValidator.class);

        IbanValidator ibanValidator = (IbanValidator) emailValidator.getNext();
        assertThat(ibanValidator.getNext()).isNull();
    }

    @Test
    void testVendorWithAuthorization(){
        assertThat(validatorConstructor.vendorWithAuthorization()).isInstanceOf(AuthorizationValidator.class);

        AuthorizationValidator authorizationValidator = (AuthorizationValidator) validatorConstructor.vendorWithAuthorization();
        assertThat(authorizationValidator.getNext()).isInstanceOf(RequestModelValidator.class);

        RequestModelValidator requestModelValidator = (RequestModelValidator) authorizationValidator.getNext();
        assertThat(requestModelValidator.getNext()).isInstanceOf(NameValidator.class);

        NameValidator nameValidator =  (NameValidator) requestModelValidator.getNext();
        assertThat(nameValidator.getNext()).isInstanceOf(SurnameValidator.class);

        SurnameValidator surnameValidator =  (SurnameValidator) nameValidator.getNext();
        assertThat(surnameValidator.getNext()).isInstanceOf(EmailValidator.class);

        EmailValidator emailValidator = (EmailValidator) surnameValidator.getNext();
        assertThat(emailValidator.getNext()).isInstanceOf(OpeningTimesValidator.class);

        OpeningTimesValidator openingTimesValidator = (OpeningTimesValidator) emailValidator.getNext();
        assertThat(openingTimesValidator.getNext()).isInstanceOf(ClosingTimesValidator.class);

        ClosingTimesValidator closingTimesValidator  = (ClosingTimesValidator) openingTimesValidator.getNext();
        assertThat(closingTimesValidator.getNext()).isInstanceOf(AddressValidator.class);

        AddressValidator addressValidator = (AddressValidator) closingTimesValidator.getNext();
        assertThat(addressValidator.getNext()).isNull();
    }
    @Test
    void testVendorWithoutAuthorization(){
        assertThat(validatorConstructor.vendorWithoutAuthorization()).isInstanceOf(RequestModelValidator.class);

        RequestModelValidator requestModelValidator = (RequestModelValidator) validatorConstructor.vendorWithoutAuthorization();
        assertThat(requestModelValidator.getNext()).isInstanceOf(NameValidator.class);

        NameValidator nameValidator =  (NameValidator) requestModelValidator.getNext();
        assertThat(nameValidator.getNext()).isInstanceOf(SurnameValidator.class);

        SurnameValidator surnameValidator =  (SurnameValidator) nameValidator.getNext();
        assertThat(surnameValidator.getNext()).isInstanceOf(EmailValidator.class);

        EmailValidator emailValidator = (EmailValidator) surnameValidator.getNext();
        assertThat(emailValidator.getNext()).isInstanceOf(OpeningTimesValidator.class);

        OpeningTimesValidator openingTimesValidator = (OpeningTimesValidator) emailValidator.getNext();
        assertThat(openingTimesValidator.getNext()).isInstanceOf(ClosingTimesValidator.class);

        ClosingTimesValidator closingTimesValidator  = (ClosingTimesValidator) openingTimesValidator.getNext();
        assertThat(closingTimesValidator.getNext()).isInstanceOf(AddressValidator.class);

        AddressValidator addressValidator = (AddressValidator) closingTimesValidator.getNext();
        assertThat(addressValidator.getNext()).isNull();
    }

}
