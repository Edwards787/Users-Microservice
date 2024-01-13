package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationData;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

public class AddressValidatorTest {


    AllergenValidator allergenValidator = new AllergenValidator();
    ValidationObject validationObject1;
    ValidationObject validationObject2;
    AuthorizationData authorizationData;
    CustomerRequestModel customerRequestModel;
    VendorRequestModel vendorRequestModel;
    Address address;

    @BeforeEach
    void setUp(){
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        customerRequestModel = new CustomerRequestModel(new Address(), new ArrayList<>(),"email", "name", "surname");

        address.setAddress("address");
        address.setCity("city");
        address.setCountry("country");
        address.setPostalCode("code");
        Location location = new Location();
        location.setLong(1F);
        location.setLat(1F);
        address.setCoordinates(location);

        vendorRequestModel = new VendorRequestModel(address, null, null, null, null, null);
        customerRequestModel = new CustomerRequestModel(address, null, null, null, null);

        validationObject1 = new ValidationObject(vendorRequestModel, authorizationData);
        validationObject2 = new ValidationObject(customerRequestModel, authorizationData);
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
}
