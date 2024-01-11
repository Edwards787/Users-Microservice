package nl.tudelft.sem.yumyumnow.users.validation;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.validation.Objects.ValidationObject;

public interface Validator {
    void setNext(Validator handler);
    boolean handle(ValidationObject validationObject) throws
        InvalidFieldException, NotAuthorizedException, MissingBodyException, UserNotFoundException;
}
