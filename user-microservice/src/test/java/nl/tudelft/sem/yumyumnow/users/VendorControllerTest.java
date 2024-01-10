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

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        assertThatThrownBy(() -> vendorController.createVendor(null))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400);
    }

    @Test
    void createVendorInvalidField() throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException {
        when(vendorService.createVendor(any())).thenThrow(new InvalidFieldException("fieldName","value"));
        assertThatThrownBy(() -> vendorController.createVendor(vendorRequestModel))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400)
                .hasFieldOrPropertyWithValue("reason", "Field 'fieldName' with value 'value' is invalid");
    }

    @Test
    void createVendorEmailAlreadyInUse() throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException {
        when(vendorService.createVendor(any())).thenThrow(new EmailAlreadyInUseException("email"));
        assertThatThrownBy(() -> vendorController.createVendor(vendorRequestModel))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 409)
                .hasFieldOrPropertyWithValue("reason", "User with email 'email' already exists");
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
        assertThatThrownBy(() -> vendorController.deleteVendor(1L, 10L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 was not found");
    }

    @Test
    void deleteVendorNotAuthorized() throws UserNotFoundException, NotAuthorizedException {
        when(vendorService.deleteVendor(any(), any())).thenThrow(new NotAuthorizedException());
        assertThatThrownBy(() -> vendorController.deleteVendor(1L, 10L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 403)
                .hasFieldOrPropertyWithValue("reason", "User with id 10 not authorized to delete vendor with id 1");
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
        assertThatThrownBy(() -> vendorController.getVendorById(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 was not found");
    }

    @Test
    void getAllVendorsSuccess() {
        when(vendorService.getAllVendors()).thenReturn(List.of(vendor));
        ResponseEntity<List<Vendor>> res = vendorController.getAllVendors();
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isEqualTo(List.of(vendor));
    }

    @Test
    void updateVendorSuccess() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doNothing().when(vendorService).updateVendor(any(), any(), any());
        ResponseEntity<Void> res = vendorController.updateVendor(1L, 1L, vendorRequestModel);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void updateVendorNotFound() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doThrow(new UserNotFoundException(1L)).when(vendorService).updateVendor(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateVendor(1L, 1L, vendorRequestModel))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 was not found");
    }

    @Test
    void updateVendorUnauthorized() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doThrow(new NotAuthorizedException()).when(vendorService).updateVendor(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateVendor(1L, 9L, vendorRequestModel))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 403)
                .hasFieldOrPropertyWithValue("reason", "User with id 9 not authorized to update vendor with id 1");
    }

    @Test
    void updateVendorMissingBody() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doThrow(new MissingBodyException()).when(vendorService).updateVendor(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateVendor(1L, 1L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400)
                .hasFieldOrPropertyWithValue("reason", "Body is missing");
    }
}
