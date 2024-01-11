package nl.tudelft.sem.yumyumnow.users.validation.Objects;

import nl.tudelft.sem.yumyumnow.users.validation.*;
import org.springframework.stereotype.Service;

@Service
public class Validators {

    public Validators() {
    }

    public Validator simpleWithoutAuthorization(){
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();

        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(null);

        return nameValidator;
    }

    public Validator simpleWithAuthorization(){
        AuthorizationValidator authorizationValidator = new AuthorizationValidator();
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();

        authorizationValidator.setNext(nameValidator);
        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(null);

        return authorizationValidator;
    }

}
