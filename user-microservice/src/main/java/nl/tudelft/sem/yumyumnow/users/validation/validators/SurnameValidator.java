package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;

public class SurnameValidator extends BaseValidator {
    @Override
    public boolean handle(ValidationObject validationObject)
            throws InvalidFieldException, NotAuthorizedException, MissingBodyException,
            UserNotFoundException {

        UserRequestModel userRequestModel = validationObject.getUserRequestModel();

        if (userRequestModel.getSurname() == null || userRequestModel.getSurname().isEmpty()) {
            throw new InvalidFieldException("surname", userRequestModel.getSurname());
        }

        return checkNext(validationObject);
    }
}
