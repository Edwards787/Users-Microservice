package nl.tudelft.sem.yumyumnow.users.validation.objects;

import nl.tudelft.sem.yumyumnow.users.validation.validators.*;
import org.springframework.stereotype.Service;

@Service
public class ValidatorConstructor {

    public ValidatorConstructor() {
    }

    /**
     * This method constructs the chain of responsibility for the user.
     *
     * @return the chain of responsibility for the user
     */
    public Validator userWithoutAuthorization() {
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

    /**
     * This method constructs the chain of responsibility for the user.
     *
     * @return the chain of responsibility for the user
     */
    public Validator userWithAuthorization() {
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

    /**
     * This method constructs the chain of responsibility for the user.
     *
     * @return the chain of responsibility for the user
     */
    public Validator userAuthorization() {
        AuthorizationValidator authorizationValidator = new AuthorizationValidator();
        authorizationValidator.setNext(null);
        return authorizationValidator;
    }

    /**
     * This method constructs the chain of responsibility for the courier.
     *
     * @return the chain of responsibility for the courier
     */
    public Validator courierWithoutAuthorization() {
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        IbanValidator ibanValidator = new IbanValidator();
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

    /**
     * This method constructs the chain of responsibility for the courier.
     *
     * @return the chain of responsibility for the courier
     */
    public Validator courierWithAuthorization() {
        AuthorizationValidator authorizationValidator = new AuthorizationValidator();
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        IbanValidator ibanValidator = new IbanValidator();
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

    /**
     * This method constructs the chain of responsibility for the vendor.
     *
     * @return the chain of responsibility for the vendor
     */
    public Validator vendorWithAuthorization() {
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

    /**
     * This method constructs the chain of responsibility for the vendor.
     *
     * @return the chain of responsibility for the vendor
     */
    public Validator vendorWithoutAuthorization() {
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

    /**
     * This method constructs the chain of responsibility for the customer.
     *
     * @return the chain of responsibility for the customer
     */
    public Validator customerWithoutAuthorization() {
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();
        AddressValidator addressValidator = new AddressValidator();
        AllergenValidator allergenValidator = new AllergenValidator();

        requestModelValidator.setNext(nameValidator);
        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(addressValidator);
        addressValidator.setNext(allergenValidator);
        addressValidator.setNext(null);

        return requestModelValidator;
    }

    /**
     * This method constructs the chain of responsibility for the customer.
     *
     * @return the chain of responsibility for the customer
     */

    public Validator customerWithAuthorization() {
        AuthorizationValidator authorizationValidator = new AuthorizationValidator();
        RequestModelValidator requestModelValidator = new RequestModelValidator();
        NameValidator nameValidator = new NameValidator();
        SurnameValidator surnameValidator = new SurnameValidator();
        EmailValidator emailValidator = new EmailValidator();
        AddressValidator addressValidator = new AddressValidator();
        AllergenValidator allergenValidator = new AllergenValidator();

        authorizationValidator.setNext(requestModelValidator);
        requestModelValidator.setNext(nameValidator);
        nameValidator.setNext(surnameValidator);
        surnameValidator.setNext(emailValidator);
        emailValidator.setNext(addressValidator);
        addressValidator.setNext(allergenValidator);
        addressValidator.setNext(null);

        return authorizationValidator;
    }

}
