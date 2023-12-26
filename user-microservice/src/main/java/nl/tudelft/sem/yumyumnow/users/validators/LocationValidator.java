package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import org.springframework.stereotype.Service;

@Service
public class LocationValidator {
    protected void validate(Location location) throws InvalidFieldException {
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
