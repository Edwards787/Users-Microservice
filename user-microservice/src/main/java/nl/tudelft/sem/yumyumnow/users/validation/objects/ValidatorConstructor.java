package nl.tudelft.sem.yumyumnow.users.validation.objects;

import nl.tudelft.sem.yumyumnow.users.validation.validators.*;
import org.springframework.stereotype.Service;

@Service
public class ValidatorConstructor {

    public ValidatorConstructor() {
    }

    public Validator userWithoutAuthorization(){
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();

        requestModelValidator.setNext(nameValidator);
        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(null);

        return requestModelValidator;
    }

    public Validator userWithAuthorization(){
        AuthorizationValidator authorizationValidator = new AuthorizationValidator();
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();


        authorizationValidator.setNext(requestModelValidator);
        requestModelValidator.setNext(nameValidator);
        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(null);

        return authorizationValidator;
    }

    public Validator userAuthorization(){
        AuthorizationValidator authorizationValidator = new AuthorizationValidator();
        authorizationValidator.setNext(null);
        return authorizationValidator;
    }

    public Validator courierWithoutAuthorization(){
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        IBANValidator ibanValidator = new IBANValidator();
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();

        requestModelValidator.setNext(nameValidator);
        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(ibanValidator);
        ibanValidator.setNext(null);

        return requestModelValidator;
    }

    public Validator courierWithAuthorization(){
        AuthorizationValidator authorizationValidator = new AuthorizationValidator();
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        IBANValidator ibanValidator = new IBANValidator();
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();

        authorizationValidator.setNext(requestModelValidator);
        requestModelValidator.setNext(nameValidator);
        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(ibanValidator);
        ibanValidator.setNext(null);

        return authorizationValidator;
    }

    public Validator vendorWithAuthorization(){
        AuthorizationValidator authorizationValidator = new AuthorizationValidator();
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();
        OpeningTimesValidator openingTimesValidator = new OpeningTimesValidator();
        ClosingTimesValidator closingTimesValidator = new ClosingTimesValidator();
        AddressValidator addressValidator = new AddressValidator();

        authorizationValidator.setNext(requestModelValidator);
        requestModelValidator.setNext(nameValidator);
        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(openingTimesValidator);
        openingTimesValidator.setNext(closingTimesValidator);
        closingTimesValidator.setNext(addressValidator);
        addressValidator.setNext(null);

        return authorizationValidator;
    }

    public Validator vendorWithoutAuthorization(){
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();
        OpeningTimesValidator openingTimesValidator = new OpeningTimesValidator();
        ClosingTimesValidator closingTimesValidator = new ClosingTimesValidator();
        AddressValidator addressValidator = new AddressValidator();

        requestModelValidator.setNext(nameValidator);
        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(openingTimesValidator);
        openingTimesValidator.setNext(closingTimesValidator);
        closingTimesValidator.setNext(addressValidator);
        addressValidator.setNext(null);

        return requestModelValidator;
    }

}
