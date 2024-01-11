package nl.tudelft.sem.yumyumnow.users.validation;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;

public abstract class BaseValidator implements Validator{
    private Validator next;

    public void setNext(Validator next){
        this.next = next;
    }

    protected boolean checkNext(ValidationObject validationObject)
        throws NotAuthorizedException, InvalidFieldException, MissingBodyException {
        if (validationObject == null){
            return true;
        }
        return next.handle(validationObject);
    }
}
