package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
/*
public class VendorValidatorTest {
    private VendorValidator vendorValidator;
    private VendorRequestModel vendorRequestModel;
    @BeforeEach
    public void setUp(){
        vendorValidator = new VendorValidator(new AddressValidator(new LocationValidator()));
        Address address = new Address()
                .address("StreetName 1A")
                .city("CityName")
                .country("CountryName")
                .postalCode("1234AB")
                .coordinates(new Location().lat(1.0f)._long(1.0f));
        vendorRequestModel = new VendorRequestModel(
                address,
                "08:00/Mo-Fr",
                "21:00/Mo-Fr",
                "email1@mail.com",
                "restaurantName",
                "surNameOwner"
        );
    }

    @Test
    public void testNoBody(){
        vendorRequestModel = null;
        assertThrows(MissingBodyException.class, () -> {
            vendorValidator.validate(vendorRequestModel);
        });
    }

    @Test
    public void testNoAddress() {
        vendorRequestModel.setAddress(null);
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'address' with value 'no content' is invalid");
    }

    @Test
    public void testNoAddressField() {
        vendorRequestModel.getAddress().setAddress("");
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'address field' with value '' is invalid");
    }

    @Test
    public void testNoCity() {
        vendorRequestModel.getAddress().setCity(null);
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'city' with value 'null' is invalid");
    }

    @Test
    public void testNoCountry() {
        vendorRequestModel.getAddress().setCountry("");
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'country' with value '' is invalid");
    }

    @Test
    public void testNoPostalCode() {
        vendorRequestModel.getAddress().setPostalCode(null);
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'postalCode' with value 'null' is invalid");
    }

    @Test
    public void testNoCoordinates() {
        vendorRequestModel.getAddress().setCoordinates(null);
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'location' with value 'no content' is invalid");
    }

    @Test
    public void testNoLatitude() {
        vendorRequestModel.getAddress().getCoordinates().setLat(null);
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'latitude' with value 'null' is invalid");
    }

    @Test
    public void testNoLongitude() {
        vendorRequestModel.getAddress().getCoordinates().setLong(null);
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'longitude' with value 'null' is invalid");
    }

    @Test
    public void testNoOpeningTime() {
        vendorRequestModel.setOpeningTimes(null);
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'opening times' with value 'null' is invalid");
    }

    @Test
    public void testNoClosingTime() {
        vendorRequestModel.setClosingTimes("");
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'closing times' with value '' is invalid");
    }

    @Test
    public void testNoEmail() {
        vendorRequestModel.setEmail("");
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'email' with value '' is invalid");
    }

    @Test
    public void testNoName() {
        vendorRequestModel.setName("");
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'name' with value '' is invalid");
    }

    @Test
    public void testNoSurname() {
        vendorRequestModel.setSurname(null);
        assertThatThrownBy(() -> vendorValidator.validate(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'surname' with value 'null' is invalid");
    }
}
*/