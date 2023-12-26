package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorValidator extends UserValidator {
    private final AddressValidator addressValidator;

    @Autowired
    public VendorValidator(AddressValidator addressValidator) {
        this.addressValidator = addressValidator;
    }

    public void validate(VendorRequestModel vendorRequestModel) throws MissingBodyException, InvalidFieldException {
        super.validate(vendorRequestModel);
        addressValidator.validate(vendorRequestModel.getAddress());
        if (vendorRequestModel.getOpeningTimes() == null || vendorRequestModel.getOpeningTimes().isEmpty()) {
            throw new InvalidFieldException("opening times", vendorRequestModel.getOpeningTimes());
        }
        if (vendorRequestModel.getClosingTimes() == null || vendorRequestModel.getClosingTimes().isEmpty()) {
            throw new InvalidFieldException("closing times", vendorRequestModel.getClosingTimes());
        }
    }
}
