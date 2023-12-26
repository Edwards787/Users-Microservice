package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import org.springframework.stereotype.Service;

@Service
public class VendorValidator extends UserValidator {
    public void validate(VendorRequestModel vendorRequestModel) throws MissingBodyException, InvalidFieldException {
        super.validate(vendorRequestModel);
    }
}
