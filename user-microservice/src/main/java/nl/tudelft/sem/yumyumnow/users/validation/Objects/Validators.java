package nl.tudelft.sem.yumyumnow.users.validation.Objects;

import nl.tudelft.sem.yumyumnow.users.validation.*;
import org.springframework.stereotype.Service;

@Service
public class Validators {

    public Validators() {
    }

    public Validator simpleWithoutAuthorization(){
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

    public Validator simpleWithAuthorization(){
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

    public Validator simpleAuthorization(){
        AuthorizationValidator authorizationValidator = new AuthorizationValidator();
        authorizationValidator.setNext(null);
        return authorizationValidator;
    }

}
