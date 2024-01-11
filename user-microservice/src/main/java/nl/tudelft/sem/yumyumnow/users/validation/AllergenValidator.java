package nl.tudelft.sem.yumyumnow.users.validation;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.ValidationObject;

public class AllergenValidator extends BaseValidator{
    @Override
    public boolean handle(ValidationObject validationObject)
        throws InvalidFieldException, NotAuthorizedException, MissingBodyException,
        UserNotFoundException {
        UserRequestModel userRequestModel = validationObject.getUserRequestModel();

        CustomerRequestModel crm = (CustomerRequestModel) userRequestModel;
        if (crm.getAllergens() == null)
            throw new InvalidFieldException("allergens", "null");
        return super.checkNext(validationObject);
    }
}
