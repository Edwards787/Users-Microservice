package nl.tudelft.sem.yumyumnow.users.validation.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class AddressValidatorTest {
    AddressValidator addressValidator = new AddressValidator();
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

        address = new Address();
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
    public void testNoAddressField() {
        address.setAddress("");
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'address field' with value '' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'address field' with value '' is invalid");
    }

    @Test
    public void testNoCity() {
        address.setCity("");
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'city' with value '' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'city' with value '' is invalid");
    }

    @Test
    public void testNoCountry() {
        address.setCountry("");
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'country' with value '' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'country' with value '' is invalid");
    }

    @Test
    public void testNoPostalCode() {
        address.setPostalCode("");
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'postalCode' with value '' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'postalCode' with value '' is invalid");
    }
    @Test
    public void testNullAddress() {
        vendorRequestModel.setAddress(null);
        customerRequestModel.setHomeAddress(null);

        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'address' with value 'no content' is invalid");

        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'address' with value 'no content' is invalid");
    }
    @Test
    public void testNullAddressField() {
        address.setAddress(null);
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'address field' with value 'null' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'address field' with value 'null' is invalid");
    }

    @Test
    public void testNullCity() {
        address.setCity(null);
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'city' with value 'null' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'city' with value 'null' is invalid");
    }

    @Test
    public void testNullCountry() {
        address.setCountry(null);
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'country' with value 'null' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'country' with value 'null' is invalid");
    }

    @Test
    public void testNullPostalCode() {
        address.setPostalCode(null);
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'postalCode' with value 'null' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'postalCode' with value 'null' is invalid");
    }
    @Test
    public void testNoCoordinates() {
        address.setCoordinates(null);
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'location' with value 'no content' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'location' with value 'no content' is invalid");
    }

    @Test
    public void testNoLatitude() {
        address.getCoordinates().setLat(null);
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'latitude' with value 'null' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'latitude' with value 'null' is invalid");
    }

    @Test
    public void testNoLongitude() {
        address.getCoordinates().setLong(null);
        assertThatThrownBy(() -> addressValidator.handle(validationObject1))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'longitude' with value 'null' is invalid");
        assertThatThrownBy(() -> addressValidator.handle(validationObject2))
            .isInstanceOf(InvalidFieldException.class)
            .hasMessage("Field 'longitude' with value 'null' is invalid");
    }
}
