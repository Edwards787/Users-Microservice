package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;

public class SurnameValidator extends BaseValidator{
    @Override
    public boolean handle(UserRequestModel u) throws MissingBodyException, InvalidFieldException {
        if(u == null){
            throw new MissingBodyException();
        }
        if (u.getSurname() == null || u.getName().equals(""))
            throw new InvalidFieldException("surname", u.getSurname());
        return super.checkNext(u);
    }
}
