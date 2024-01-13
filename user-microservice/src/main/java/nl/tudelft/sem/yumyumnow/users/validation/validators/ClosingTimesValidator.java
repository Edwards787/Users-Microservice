package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.ValidationObject;

public class ClosingTimesValidator extends BaseValidator{
    @Override
    public boolean handle(ValidationObject validationObject)
        throws InvalidFieldException, NotAuthorizedException, MissingBodyException,
        UserNotFoundException {

        VendorRequestModel vendorRequestModel = (VendorRequestModel) validationObject.getUserRequestModel();
        if (vendorRequestModel.getClosingTimes() == null || vendorRequestModel.getClosingTimes().isEmpty()) {
            throw new InvalidFieldException("closing times", vendorRequestModel.getClosingTimes());
        }
        return super.checkNext(validationObject);
    }
}
