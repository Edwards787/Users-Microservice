package nl.tudelft.sem.yumyumnow.users.validation;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;

public class NameValidator extends BaseValidator {

    @Override
    public boolean handle(ValidationObject validationObject)
        throws InvalidFieldException, NotAuthorizedException, MissingBodyException {

        UserRequestModel userRequestModel = validationObject.getUserRequestModel();
        if (userRequestModel.getName() == null || userRequestModel.getName().isEmpty()) {
            throw new InvalidFieldException("name", userRequestModel.getName());
        }

        return super.checkNext(validationObject);
    }
}
