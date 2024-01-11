package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import org.springframework.beans.factory.annotation.Autowired;

public class AddressValidator{
    private final LocationValidator locationValidator;

    @Autowired
    public AddressValidator(LocationValidator locationValidator) {
        this.locationValidator = locationValidator;
    }

    protected void validate(Address address) throws InvalidFieldException {
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
        locationValidator.validate(address.getCoordinates());
    }
}
