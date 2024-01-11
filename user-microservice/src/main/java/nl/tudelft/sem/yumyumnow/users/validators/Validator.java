package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;

public interface Validator {

    void setNext(Validator handler);
    boolean handle(UserRequestModel user) throws MissingBodyException, InvalidFieldException;
}
