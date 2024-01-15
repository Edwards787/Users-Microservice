package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.services.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VendorControllerTest {
    transient VendorController vendorController;
    transient VendorService vendorService;
    transient Vendor vendor;
    transient VendorRequestModel vendorRequestModel;
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
    void createVendorSuccess()
        throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {
        when(vendorService.createVendor(vendorRequestModel)).thenReturn(vendor);
        ResponseEntity<Vendor> res = vendorController.createVendor(vendorRequestModel);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isEqualTo(vendor);
    }

    @Test
    void createVendorMissingBody()
        throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {
        when(vendorService.createVendor(any())).thenThrow(new MissingBodyException());
        assertThatThrownBy(() -> vendorController.createVendor(null))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400);
    }

    @Test
    void createVendorInvalidField()
        throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {
        when(vendorService.createVendor(any())).thenThrow(new InvalidFieldException("fieldName","value"));
        assertThatThrownBy(() -> vendorController.createVendor(vendorRequestModel))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400)
                .hasFieldOrPropertyWithValue("reason", "Field 'fieldName' with value 'value' is invalid");
    }

    @Test
    void createVendorEmailAlreadyInUse()
        throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {
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
    void deleteVendorNotFound()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        when(vendorService.deleteVendor(any(), any())).thenThrow(new UserNotFoundException(1L));
        assertThatThrownBy(() -> vendorController.deleteVendor(1L, 10L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 not found");
    }

    @Test
    void deleteVendorNotAuthorized()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
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
                .hasFieldOrPropertyWithValue("reason", "User with id 1 not found");
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
                .hasFieldOrPropertyWithValue("reason", "User with id 1 not found");
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

    @Test
    void getVendorsByRadiusSuccess() throws InvalidFieldException, ContentNotFoundException {
        when(vendorService.getVendorsByRadius(any(), any(), any())).thenReturn(List.of(vendor));
        ResponseEntity<List<Vendor>> res = vendorController.getVendorsByRadius(1L, 1.0f, 20.0f);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isEqualTo(List.of(vendor));
    }

    @Test
    void getVendorsByRadiusInvalidField() throws InvalidFieldException, ContentNotFoundException {
        when(vendorService.getVendorsByRadius(any(), any(), any())).thenThrow(new InvalidFieldException("fieldName", "value"));
        assertThatThrownBy(() -> vendorController.getVendorsByRadius(1L, 1.0f, 20.0f))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400)
                .hasFieldOrPropertyWithValue("reason", "Field 'fieldName' with value 'value' is invalid");
    }

    @Test
    void getVendorsByRadiusNotFound() throws InvalidFieldException, ContentNotFoundException {
        doThrow(new ContentNotFoundException("No vendors in radius")).when(vendorService).getVendorsByRadius(any(), any(), any());
        assertThatThrownBy(() -> vendorController.getVendorsByRadius(1L, 1.0f, 20.0f))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "No vendors in radius");
    }

    @Test
    void uploadValidationByVendorSuccess() throws UserNotFoundException, InvalidFieldException, MissingBodyException {
        doNothing().when(vendorService).uploadValidationByVendor(any(), any());
        ResponseEntity<Void> res = vendorController.uploadValidationByVendor(1L, null);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void uploadValidationByVendorNotFound() throws UserNotFoundException, InvalidFieldException, MissingBodyException {
        doThrow(new UserNotFoundException(1L)).when(vendorService).uploadValidationByVendor(any(), any());
        assertThatThrownBy(() -> vendorController.uploadValidationByVendor(1L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 not found");
    }

    @Test
    void uploadValidationByVendorInvalidField() throws UserNotFoundException, InvalidFieldException, MissingBodyException {
        doThrow(new InvalidFieldException("fieldName", "value")).when(vendorService).uploadValidationByVendor(any(), any());
        assertThatThrownBy(() -> vendorController.uploadValidationByVendor(1L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400)
                .hasFieldOrPropertyWithValue("reason", "Field 'fieldName' with value 'value' is invalid");
    }

    @Test
    void getValidationByVendor() throws UserNotFoundException, ContentNotFoundException {
        Validation validation = new Validation();
        validation.setVendorId(1L);
        validation.setDocument("document");
        when(vendorService.getValidationByVendor(any())).thenReturn(validation);
        ResponseEntity<Validation> res = vendorController.getValidationByVendor(1L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isEqualTo(validation);
    }

    @Test
    void getValidationByVendorNotFound() throws UserNotFoundException, ContentNotFoundException {
        doThrow(new UserNotFoundException(1L)).when(vendorService).getValidationByVendor(any());
        assertThatThrownBy(() -> vendorController.getValidationByVendor(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 not found");
    }

    @Test
    void getValidationByVendorContentNotFound() throws UserNotFoundException, ContentNotFoundException {
        doThrow(new ContentNotFoundException("No validation found")).when(vendorService).getValidationByVendor(any());
        assertThatThrownBy(() -> vendorController.getValidationByVendor(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "No validation found");
    }

    @Test
    void updateValidationByVendorSuccess() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doNothing().when(vendorService).updateValidationByVendor(any(), any(), any());
        ResponseEntity<Void> res = vendorController.updateValidationByVendor(1L, 1L, null);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void updateValidationByVendorNotFound() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doThrow(new UserNotFoundException(1L)).when(vendorService).updateValidationByVendor(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateValidationByVendor(1L, 1L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 not found");
    }

    @Test
    void updateValidationByVendorUnauthorized() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doThrow(new NotAuthorizedException()).when(vendorService).updateValidationByVendor(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateValidationByVendor(1L, 9L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 403)
                .hasFieldOrPropertyWithValue("reason", "User with id 9 not authorized to update validation with id 1");
    }

    @Test
    void updateValidationByVendorMissingBody() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        doThrow(new MissingBodyException()).when(vendorService).updateValidationByVendor(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateValidationByVendor(1L, 1L, null))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400)
                .hasFieldOrPropertyWithValue("reason", "Body is missing");
    }

    @Test
    void deleteValidationByVendorSuccess() throws UserNotFoundException, NotAuthorizedException {
        doNothing().when(vendorService).deleteValidationByVendor(any(), any());
        ResponseEntity<Void> res = vendorController.deleteValidationByVendor(1L, 1L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void deleteValidationByVendorNotFound() throws UserNotFoundException, NotAuthorizedException {
        doThrow(new UserNotFoundException(1L)).when(vendorService).deleteValidationByVendor(any(), any());
        assertThatThrownBy(() -> vendorController.deleteValidationByVendor(1L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 not found");
    }

    @Test
    void deleteValidationByVendorUnauthorized() throws UserNotFoundException, NotAuthorizedException {
        doThrow(new NotAuthorizedException()).when(vendorService).deleteValidationByVendor(any(), any());
        assertThatThrownBy(() -> vendorController.deleteValidationByVendor(1L, 9L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 403)
                .hasFieldOrPropertyWithValue("reason", "User with id 9 not authorized to delete validation with id 1");
    }

    @Test
    void updateVendorActivationStatusSuccess() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        doNothing().when(vendorService).updateVendorActivationStatus(any(), any(), any());
        ResponseEntity<Void> res = vendorController.updateVendorActivationStatus(1L, 1L, true);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void updateVendorActivationStatusNotFound() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        doThrow(new UserNotFoundException(1L)).when(vendorService).updateVendorActivationStatus(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateVendorActivationStatus(1L, 1L, true))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 not found");
    }

    @Test
    void updateVendorActivationStatusUnauthorized() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        doThrow(new NotAuthorizedException()).when(vendorService).updateVendorActivationStatus(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateVendorActivationStatus(1L, 9L, true))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 403)
                .hasFieldOrPropertyWithValue("reason", "User with id 9 not authorized to update activation status of vendor with id 1");
    }

    @Test
    void updateVendorActivationStatusMissingBody() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        doThrow(new MissingBodyException()).when(vendorService).updateVendorActivationStatus(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateVendorActivationStatus(1L, 1L, true))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400)
                .hasFieldOrPropertyWithValue("reason", "Body is missing");
    }

    @Test
    void updateVendorBlockStatusSuccess() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        doNothing().when(vendorService).updateVendorBlockStatus(any(), any(), any());
        ResponseEntity<Void> res = vendorController.updateVendorBlockStatus(1L, 1L, true);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isNull();
    }

    @Test
    void updateVendorBlockStatusNotFound() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        doThrow(new UserNotFoundException(1L)).when(vendorService).updateVendorBlockStatus(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateVendorBlockStatus(1L, 1L, true))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 404)
                .hasFieldOrPropertyWithValue("reason", "User with id 1 not found");
    }

    @Test
    void updateVendorBlockStatusUnauthorized() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        doThrow(new NotAuthorizedException()).when(vendorService).updateVendorBlockStatus(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateVendorBlockStatus(1L, 9L, true))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 403)
                .hasFieldOrPropertyWithValue("reason", "User with id 9 not authorized to update block status of vendor with id 1");
    }

    @Test
    void updateVendorBlockStatusMissingBody() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        doThrow(new MissingBodyException()).when(vendorService).updateVendorBlockStatus(any(), any(), any());
        assertThatThrownBy(() -> vendorController.updateVendorBlockStatus(1L, 1L, true))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status.value", 400)
                .hasFieldOrPropertyWithValue("reason", "Body is missing");
    }
}
