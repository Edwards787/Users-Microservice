package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.Validator;

public abstract class BaseValidator implements Validator {
    private transient Validator next;

    public Validator getNext() {
        return next;
    }

    public void setNext(Validator next) {
        this.next = next;
    }

    protected boolean checkNext(ValidationObject validationObject)
            throws NotAuthorizedException, InvalidFieldException, MissingBodyException,
            UserNotFoundException {
        if (next == null) {
            return true;
        }
        return next.handle(validationObject);
    }
}
