package nl.tudelft.sem.yumyumnow.users.validation;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.ValidationObject;

import java.util.regex.Pattern;

public class EmailValidator extends BaseValidator{

    @Override
    public boolean handle(ValidationObject validationObject)
        throws InvalidFieldException, NotAuthorizedException, MissingBodyException,
        UserNotFoundException {

        UserRequestModel userRequestModel = validationObject.getUserRequestModel();
        if (userRequestModel.getEmail() == null
            || userRequestModel.getEmail().isEmpty()
            || !Pattern.compile("^(.+)@(\\S+)$")
            .matcher(userRequestModel.getEmail())
            .matches()) {
            throw new InvalidFieldException("email", userRequestModel.getEmail());
        }
        return super.checkNext(validationObject);
    }
}
