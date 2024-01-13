package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;

public class IbanValidator extends BaseValidator {

    @Override
    public boolean handle(ValidationObject validationObject)
            throws InvalidFieldException, NotAuthorizedException, MissingBodyException,
            UserNotFoundException {

        CourierRequestModel courierRequestModel = (CourierRequestModel) validationObject.getUserRequestModel();
        String iban = courierRequestModel.getIbanNumber();

        if (iban == null) {
            throw new InvalidFieldException("iban", "null");
        }

        return super.checkNext(validationObject);
    }
}
