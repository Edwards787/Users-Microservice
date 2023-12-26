package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.validators.AddressValidator;
import nl.tudelft.sem.yumyumnow.users.validators.LocationValidator;
import nl.tudelft.sem.yumyumnow.users.validators.VendorValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'address' with value 'no content' is invalid");
        }
    }

    @Test
    public void testNoAddressField() {
        vendorRequestModel.getAddress().setAddress("");
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'address field' with value '' is invalid");
        }
    }

    @Test
    public void testNoCity() {
        vendorRequestModel.getAddress().setCity(null);
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'city' with value 'null' is invalid");
        }
    }

    @Test
    public void testNoCountry() {
        vendorRequestModel.getAddress().setCountry("");
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'country' with value '' is invalid");
        }
    }

    @Test
    public void testNoPostalCode() {
        vendorRequestModel.getAddress().setPostalCode(null);
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'postalCode' with value 'null' is invalid");
        }
    }

    @Test
    public void testNoCoordinates() {
        vendorRequestModel.getAddress().setCoordinates(null);
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'location' with value 'no content' is invalid");
        }
    }

    @Test
    public void testNoLatitude() {
        vendorRequestModel.getAddress().getCoordinates().setLat(null);
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'latitude' with value 'null' is invalid");
        }
    }

    @Test
    public void testNoLongitude() {
        vendorRequestModel.getAddress().getCoordinates().setLong(null);
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'longitude' with value 'null' is invalid");
        }
    }

    @Test
    public void testNoOpeningTime() {
        vendorRequestModel.setOpeningTimes(null);
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'opening times' with value 'null' is invalid");
        }
    }

    @Test
    public void testNoClosingTime() {
        vendorRequestModel.setClosingTimes("");
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'closing times' with value '' is invalid");
        }
    }

    @Test
    public void testNoEmail() {
        vendorRequestModel.setEmail("");
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'email' with value '' is invalid");
        }
    }

    @Test
    public void testNoName() {
        vendorRequestModel.setName("");
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'name' with value '' is invalid");
        }
    }

    @Test
    public void testNoSurname() {
        vendorRequestModel.setSurname(null);
        try {
            vendorValidator.validate(vendorRequestModel);
        } catch (Exception e){
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assertThat(e.getMessage()).isEqualTo("Field 'surname' with value 'null' is invalid");
        }
    }
}
