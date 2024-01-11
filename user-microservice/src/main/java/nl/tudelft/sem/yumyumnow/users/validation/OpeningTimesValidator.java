package nl.tudelft.sem.yumyumnow.users.validation;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;

public class OpeningTimesValidator extends BaseValidator{
    @Override
    public boolean handle(ValidationObject validationObject)
        throws InvalidFieldException, NotAuthorizedException, MissingBodyException {

        VendorRequestModel vendorRequestModel = (VendorRequestModel) validationObject.getUserRequestModel();

        if (vendorRequestModel.getOpeningTimes() == null || vendorRequestModel.getOpeningTimes().isEmpty()) {
            throw new InvalidFieldException("opening times", vendorRequestModel.getOpeningTimes());
        }

        return super.checkNext(validationObject);
    }
}
