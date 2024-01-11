package nl.tudelft.sem.yumyumnow.users.validation.Objects;

import nl.tudelft.sem.yumyumnow.users.validation.*;
import org.springframework.stereotype.Service;

@Service
public class Validators {

    public Validators() {
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

}
