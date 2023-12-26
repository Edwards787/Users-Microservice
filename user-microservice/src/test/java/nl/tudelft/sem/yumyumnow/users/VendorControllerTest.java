package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.controllers.VendorController;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.services.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VendorControllerTest {
    private VendorController vendorController;
    private VendorService vendorService;
    private Vendor vendor;
    private VendorRequestModel vendorRequestModel;
    @BeforeEach
    public void setUp() {
        vendorService = mock(VendorService.class);
        vendorController = new VendorController(vendorService);

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
        vendor = new Vendor()
                .id(1L)
                .address(address)
                .openingTimes("08:00/Mo-Fr")
                .closingTimes("21:00/Mo-Fr")
                .email("email1@mail.com")
                .name("restaurantName")
                .surname("surNameOwner")
                .role(Vendor.RoleEnum.VENDOR)
                .blocked(false)
                .activated(false);
    }

    @Test
    void createVendorSuccess() throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException {
        when(vendorService.createVendor(vendorRequestModel)).thenReturn(vendor);
        ResponseEntity<Vendor> res = vendorController.createVendor(vendorRequestModel);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isEqualTo(vendor);
    }

    @Test
    void createVendorMissingBody() throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException {
        when(vendorService.createVendor(any())).thenThrow(new MissingBodyException());
        try{
            vendorController.createVendor(vendorRequestModel);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResponseStatusException.class);
            ResponseStatusException ex = (ResponseStatusException) e;
            assertThat(ex.getStatus().value()).isEqualTo(400);
        }
    }

    @Test
    void createVendorInvalidField() throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException {
        when(vendorService.createVendor(any())).thenThrow(new InvalidFieldException("fieldName","value"));
        try{
            vendorController.createVendor(vendorRequestModel);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResponseStatusException.class);
            ResponseStatusException ex = (ResponseStatusException) e;
            assertThat(ex.getStatus().value()).isEqualTo(400);
            assertThat(ex.getReason()).isEqualTo("Field 'fieldName' with value 'value' is invalid");
        }
    }

    @Test
    void createVendorEmailAlreadyInUse() throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException {
        when(vendorService.createVendor(any())).thenThrow(new EmailAlreadyInUseException("email"));
        try{
            vendorController.createVendor(vendorRequestModel);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResponseStatusException.class);
            ResponseStatusException ex = (ResponseStatusException) e;
            assertThat(ex.getStatus().value()).isEqualTo(409);
            assertThat(ex.getReason()).isEqualTo("User with email 'email' already exists");
        }
    }

    @Test
    void deleteVendorSuccess() {
        ResponseEntity<Void> res = vendorController.deleteVendor(1L, 1L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void deleteVendorNotFound() throws UserNotFoundException, NotAuthorizedException {
        when(vendorService.deleteVendor(any(), any())).thenThrow(new UserNotFoundException(1L));
        try{
            vendorController.deleteVendor(1L, 10L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResponseStatusException.class);
            ResponseStatusException ex = (ResponseStatusException) e;
            assertThat(ex.getStatus().value()).isEqualTo(404);
            assertThat(ex.getReason()).isEqualTo("User with id 1 was not found");
        }
    }

    @Test
    void deleteVendorNotAuthorized() throws UserNotFoundException, NotAuthorizedException {
        when(vendorService.deleteVendor(any(), any())).thenThrow(new NotAuthorizedException());
        try{
            vendorController.deleteVendor(1L, 10L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResponseStatusException.class);
            ResponseStatusException ex = (ResponseStatusException) e;
            assertThat(ex.getStatus().value()).isEqualTo(403);
            assertThat(ex.getReason()).isEqualTo("User with id 10 not authorized to delete vendor with id 1");
        }
    }

    @Test
    void getVendorByIdSuccess() throws UserNotFoundException {
        when(vendorService.getVendorById(1L)).thenReturn(vendor);
        ResponseEntity<Vendor> res = vendorController.getVendorById(1L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isEqualTo(vendor);
    }

    @Test
    void getVendorByIdNotFound() throws UserNotFoundException {
        when(vendorService.getVendorById(1L)).thenThrow(new UserNotFoundException(1L));
        try{
            vendorController.getVendorById(1L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ResponseStatusException.class);
            ResponseStatusException ex = (ResponseStatusException) e;
            assertThat(ex.getStatus().value()).isEqualTo(404);
            assertThat(ex.getReason()).isEqualTo("User with id 1 was not found");
        }
    }
}
