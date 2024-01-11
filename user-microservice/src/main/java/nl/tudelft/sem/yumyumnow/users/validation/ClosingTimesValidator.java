package nl.tudelft.sem.yumyumnow.users.validation;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;

public class ClosingTimesValidator extends BaseValidator{
    @Override
    public boolean handle(ValidationObject validationObject)
        throws InvalidFieldException, NotAuthorizedException, MissingBodyException {

        VendorRequestModel vendorRequestModel = (VendorRequestModel) validationObject.getUserRequestModel();
        if (vendorRequestModel.getClosingTimes() == null || vendorRequestModel.getClosingTimes().isEmpty()) {
            throw new InvalidFieldException("closing times", vendorRequestModel.getClosingTimes());
        }
        return super.checkNext(validationObject);
    }
}
