package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;

public class AddressValidator extends BaseValidator{
    @Override
    public boolean handle(UserRequestModel u) throws MissingBodyException, InvalidFieldException {
        if(u == null){
            throw new MissingBodyException();
        }
        if(u.getClass() != CustomerRequestModel.class){
            throw new InvalidFieldException("Wrong role", u.getClass().toString());
        }
        CustomerRequestModel crm = (CustomerRequestModel) u;
        if(crm.getHomeAddress() == null){
            throw new InvalidFieldException("address", crm.getHomeAddress().getAddress());
        }
        return super.checkNext(u);
    }
}
