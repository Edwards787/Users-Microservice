package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;

public abstract class BaseValidator implements Validator{
    private Validator next;
    public void setNext(Validator h){
        this.next = h;
    }

    protected boolean checkNext(UserRequestModel u) throws MissingBodyException, InvalidFieldException {
        if(next == null){
            return true;
        }
        return next.handle(u);
    }
}
