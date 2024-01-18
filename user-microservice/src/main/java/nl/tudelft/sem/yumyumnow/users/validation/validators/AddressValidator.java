package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;

public class AddressValidator extends BaseValidator {
    @Override
    public boolean handle(ValidationObject validationObject)
            throws InvalidFieldException, NotAuthorizedException, MissingBodyException,
            UserNotFoundException {

        Address address;
        if (validationObject.getUserRequestModel().getClass() == CustomerRequestModel.class) {
            CustomerRequestModel customerRequestModel = (CustomerRequestModel) validationObject.getUserRequestModel();
            address = customerRequestModel.getHomeAddress();
        } else if (validationObject.getUserRequestModel().getClass() == VendorRequestModel.class) {
            VendorRequestModel vendorRequestModel = (VendorRequestModel) validationObject.getUserRequestModel();
            address = vendorRequestModel.getAddress();
        } else {
            throw new InvalidFieldException("address", "no content");
        }

        if (address == null) {
            throw new InvalidFieldException("address", "no content");
        }
        if (address.getAddress() == null || address.getAddress().isEmpty()) {
            throw new InvalidFieldException("address field", String.valueOf(address.getAddress()));
        }
        if (address.getCity() == null || address.getCity().isEmpty()) {
            throw new InvalidFieldException("city", String.valueOf(address.getCity()));
        }
        if (address.getCountry() == null || address.getCountry().isEmpty()) {
            throw new InvalidFieldException("country", String.valueOf(address.getCountry()));
        }
        if (address.getPostalCode() == null || address.getPostalCode().isEmpty()) {
            throw new InvalidFieldException("postalCode", String.valueOf(address.getPostalCode()));
        }
        validateLocation(address.getCoordinates());

        return checkNext(validationObject);
    }

    private void validateLocation(Location location) throws InvalidFieldException {
        if (location == null) {
            throw new InvalidFieldException("location", "no content");
        }
        if (location.getLat() == null) {
            throw new InvalidFieldException("latitude", String.valueOf(location.getLat()));
        }
        if (location.getLong() == null) {
            throw new InvalidFieldException("longitude", String.valueOf(location.getLong()));
        }
    }
}
