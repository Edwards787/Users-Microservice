package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationData;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.ValidationObject;

public class AuthorizationValidator extends BaseValidator{

    @Override
    public boolean handle(ValidationObject validationObject)
        throws InvalidFieldException, NotAuthorizedException, MissingBodyException,
        UserNotFoundException {

        AuthorizationData data = validationObject.getAuthorizationData();
        data.getAuthorizationStrategy().authorize(data.getId1(), data.getId2(),
            data.getUserRepository());

        return super.checkNext(validationObject);
    }
}
