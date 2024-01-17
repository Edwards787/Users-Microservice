package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.users.builders.email.Email;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.ValidationRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class VendorServiceTest {
    transient VendorRepository vendorRepository;
    transient UserRepository userRepository;
    transient ValidationRepository validationRepository;
    transient VendorBuilder vendorBuilder;
    transient VendorService vendorService;
    transient Vendor vendor;
    transient VendorRequestModel vendorRequestModel;
    transient EmailService emailService;
    @BeforeEach
    public void setUp() {
        vendorRepository = mock(VendorRepository.class);
        validationRepository = mock(ValidationRepository.class);
        vendorBuilder = new VendorBuilder();
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        vendorService = new VendorService(vendorRepository, userRepository, vendorBuilder, validationRepository, emailService);

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
    void createVendor()
        throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {
        when(vendorRepository.findByEmail(vendorRequestModel.getEmail())).thenReturn(Optional.empty());
        when(vendorRepository.saveAndFlush(vendorBuilder.build(new Vendor(), vendorRequestModel))).thenReturn(vendor);
        assertThat(vendorService.createVendor(vendorRequestModel)).isEqualTo(vendor);
        verify(vendorRepository, times(1)).saveAndFlush(any());
        verify(emailService).sendEmail(argThat(x -> x.getRecipient().equals(vendorRequestModel.getEmail())));
    }

    @Test
    void createVendorExistingEmail() {
        when(vendorRepository.findByEmail(vendorRequestModel.getEmail())).thenReturn(Optional.of(vendor));
        assertThatThrownBy(() -> vendorService.createVendor(vendorRequestModel))
                .isInstanceOf(EmailAlreadyInUseException.class)
                .hasMessage("User with email '" + vendorRequestModel.getEmail()  + "' already exists")
                .hasFieldOrPropertyWithValue("email", vendorRequestModel.getEmail());
    }

    @Test
    void createVendorMissingBody() {
        assertThatThrownBy(() -> vendorService.createVendor(null))
                .isInstanceOf(MissingBodyException.class)
                .hasMessage("Body is missing");
    }

    @Test
    void createVendorInvalidField() {
        when(vendorRepository.findByEmail(vendorRequestModel.getEmail())).thenReturn(Optional.empty());
        vendorRequestModel.setEmail("invalidEmail");
        assertThatThrownBy(() -> vendorService.createVendor(vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'email' with value 'invalidEmail' is invalid")
                .hasFieldOrPropertyWithValue("fieldName", "email");
    }

    @Test
    void getVendorById() throws UserNotFoundException {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        assertThat(vendorService.getVendorById(1L)).isEqualTo(vendor);
    }

    @Test
    void getVendorByIdNotFound() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vendorService.getVendorById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found")
                .hasFieldOrPropertyWithValue("targetId", 1L);
    }

    @Test
    void deleteVendorUnauthorized() throws UserNotFoundException, NotAuthorizedException {
        when(userRepository.findById(2L)).thenReturn(Optional.of(vendor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));

        assertThatThrownBy(() -> vendorService.deleteVendor(1L, 2L))
                .isInstanceOf(NotAuthorizedException.class)
                .hasMessage("Not authorized");
        verify(vendorRepository, times(0)).deleteById(1L);
    }

    @Test
    void deleteVendor() throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(vendor));
        assertThat(vendorService.deleteVendor(1L, 1L)).isNull();
        verify(vendorRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllVendors() {
        vendorService.getAllVendors();
        verify(vendorRepository, times(1)).findAll();
    }

    @Test
    void updateVendorValid() throws UserNotFoundException, NotAuthorizedException, InvalidFieldException, MissingBodyException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        vendorRequestModel.setEmail("newEmail@email.com");
        assertThat(vendorService.updateVendor(1L, 1L, vendorRequestModel)).isNull();
        ArgumentCaptor<Vendor> vendorCaptor = ArgumentCaptor.forClass(Vendor.class);
        verify(vendorRepository, times(1)).saveAndFlush(vendorCaptor.capture());
        assertThat(vendorCaptor.getValue().getEmail()).isEqualTo("newEmail@email.com");
    }

    @Test
    void updateVendorInvalid() throws UserNotFoundException, NotAuthorizedException, InvalidFieldException, MissingBodyException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        vendorRequestModel.setEmail("invalidEmail");
        assertThatThrownBy(() -> vendorService.updateVendor(1L, 1L, vendorRequestModel))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'email' with value 'invalidEmail' is invalid")
                .hasFieldOrPropertyWithValue("fieldName", "email");
    }

    @Test
    void getVendorsByRadiusInvalidFields() {
        assertThatThrownBy(() -> vendorService.getVendorsByRadius(null, 1.0f, 1.0f))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'radius' with value 'null' is invalid");
        assertThatThrownBy(() -> vendorService.getVendorsByRadius(1L, -91f, 1.0f))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'latitude' with value '-91.0' is invalid");
        assertThatThrownBy(() -> vendorService.getVendorsByRadius(1L, -90.0f, 180.1f))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'longitude' with value '180.1' is invalid");
    }

    @Test
    void getVendorsByRadius() throws InvalidFieldException, ContentNotFoundException {
        when(vendorRepository.findAll()).thenReturn(
                java.util.List.of(
                        new Vendor().id(1L).address(new Address().coordinates(new Location().lat(40.7128f)._long(-74.0060f))),
                        new Vendor().id(2L).address(new Address().coordinates(new Location().lat(34.0522f)._long(-118.2437f))),
                        new Vendor().id(3L).address(new Address().coordinates(new Location().lat(40.8781f)._long(-74.6298f))),
                        new Vendor().id(4L).address(new Address().coordinates(new Location().lat(37.7749f)._long(-122.4194f))),
                        new Vendor().id(5L).address(new Address().coordinates(new Location().lat(40.7128f)._long(-74.0080f)))
                )
        );
        assertThat(vendorService.getVendorsByRadius(100L, 40.7128f, -74.0060f)).containsExactlyInAnyOrderElementsOf(
                java.util.List.of(
                        new Vendor().id(1L).address(new Address().coordinates(new Location().lat(40.7128f)._long(-74.0060f))),
                        new Vendor().id(3L).address(new Address().coordinates(new Location().lat(40.8781f)._long(-74.6298f))),
                        new Vendor().id(5L).address(new Address().coordinates(new Location().lat(40.7128f)._long(-74.0080f)))
                ));
        verify(vendorRepository, times(1)).findAll();
    }

    @Test
    void getVendorsByRadiusNone() throws InvalidFieldException, ContentNotFoundException {
        when(vendorRepository.findAll()).thenReturn(
                java.util.List.of(
                        new Vendor().id(1L).address(new Address().coordinates(new Location().lat(40.7128f)._long(-74.0060f))),
                        new Vendor().id(2L).address(new Address().coordinates(new Location().lat(34.0522f)._long(-118.2437f))),
                        new Vendor().id(3L).address(new Address().coordinates(new Location().lat(40.8781f)._long(-74.6298f))),
                        new Vendor().id(4L).address(new Address().coordinates(new Location().lat(37.7749f)._long(-122.4194f))),
                        new Vendor().id(5L).address(new Address().coordinates(new Location().lat(40.7128f)._long(-74.0080f)))
                )
        );
        assertThatThrownBy(() -> vendorService.getVendorsByRadius(100L, 0.0f, 0.0f))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessage("No vendors found in the given radius");
        verify(vendorRepository, times(1)).findAll();
    }

    @Test
    void uploadValidationByVendorNullUser() {
        assertThatThrownBy(() -> vendorService.uploadValidationByVendor(null, new ValidationRequestModel()))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'userId' with value 'null' is invalid");
    }

    @Test
    void uploadValidationByVendorNullValidation() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        assertThatThrownBy(() -> vendorService.uploadValidationByVendor(1L, null))
                .isInstanceOf(MissingBodyException.class)
                .hasMessage("Body is missing");
    }

    @Test
    void uploadValidationByVendorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vendorService.uploadValidationByVendor(1L, new ValidationRequestModel()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found")
                .hasFieldOrPropertyWithValue("targetId", 1L);
    }

    @Test
    void uploadValidationByVendorEmptyValidation() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        ValidationRequestModel vrm = new ValidationRequestModel();
        vrm.setDocument("");
        assertThatThrownBy(() -> vendorService.uploadValidationByVendor(1L, vrm))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'document' with value '' is invalid");
    }

    @Test
    void uploadValidationByVendor() throws UserNotFoundException, InvalidFieldException, MissingBodyException {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        ValidationRequestModel vrm = new ValidationRequestModel();
        vrm.setDocument("document");
        assertThat(vendorService.uploadValidationByVendor(1L, vrm)).isNull();
        ArgumentCaptor<Validation> validationCaptor = ArgumentCaptor.forClass(Validation.class);
        verify(validationRepository, times(1)).saveAndFlush(validationCaptor.capture());
        assertThat(validationCaptor.getValue().getVendorId()).isEqualTo(1L);
        assertThat(validationCaptor.getValue().getDocument()).isEqualTo("document");
    }

    @Test
    void getValidationByVendorNullUser() {
        assertThatThrownBy(() -> vendorService.getValidationByVendor(null))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void getValidationByVendorNotFound() {
        when(vendorRepository.existsById(1L)).thenReturn(Boolean.FALSE);
        assertThatThrownBy(() -> vendorService.getValidationByVendor(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found")
                .hasFieldOrPropertyWithValue("targetId", 1L);
    }

    @Test
    void getValidationByVendorNoValidation() throws UserNotFoundException, ContentNotFoundException {
        when(vendorRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when(validationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vendorService.getValidationByVendor(1L))
                .isInstanceOf(ContentNotFoundException.class)
                .hasMessage("Validation not found");
    }

    @Test
    void getValidationByVendor() throws UserNotFoundException, ContentNotFoundException {
        when(vendorRepository.existsById(1L)).thenReturn(Boolean.TRUE);
        when(validationRepository.findById(1L)).thenReturn(Optional.of(new Validation().document("document")));
        assertThat(vendorService.getValidationByVendor(1L)).isEqualTo(new Validation().document("document"));
    }

    @Test
    void updateValidationByVendorNullValidation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        assertThatThrownBy(() -> vendorService.updateValidationByVendor(1L, 1L, null))
                .isInstanceOf(MissingBodyException.class)
                .hasMessage("Body is missing");
    }

    @Test
    void updateValidationByVendorEmptyValidation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        ValidationRequestModel vrm = new ValidationRequestModel();
        vrm.setDocument("");
        assertThatThrownBy(() -> vendorService.updateValidationByVendor(1L, 1L, vrm))
                .isInstanceOf(InvalidFieldException.class)
                .hasMessage("Field 'document' with value '' is invalid");
    }

    @Test
    void updateValidationByVendor() throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        ValidationRequestModel vrm = new ValidationRequestModel();
        vrm.setDocument("document");
        assertThat(vendorService.updateValidationByVendor(1L, 1L, vrm)).isNull();
        ArgumentCaptor<Validation> validationCaptor = ArgumentCaptor.forClass(Validation.class);
        verify(validationRepository, times(1)).saveAndFlush(validationCaptor.capture());
        assertThat(validationCaptor.getValue().getVendorId()).isEqualTo(1L);
        assertThat(validationCaptor.getValue().getDocument()).isEqualTo("document");
    }

    @Test
    void deleteValidationByVendor() throws UserNotFoundException, NotAuthorizedException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        assertThat(vendorService.deleteValidationByVendor(1L, 1L)).isNull();
        verify(validationRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllValidations() {
        vendorService.getAllValidations();
        verify(validationRepository, times(1)).findAll();
    }

    @Test
    void updateVendorActivationStatusNullBody() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(userRepository.findById(2L)).thenReturn(Optional.of(
                new User().id(2L).role(User.RoleEnum.ADMIN)
        ));
        assertThatThrownBy(() -> vendorService.updateVendorActivationStatus(1L, 2L, null))
                .isInstanceOf(MissingBodyException.class)
                .hasMessage("Body is missing");
    }

    @Test
    void updateVendorActivationStatus() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(userRepository.findById(2L)).thenReturn(Optional.of(
                new User().id(2L).role(User.RoleEnum.ADMIN)
        ));
        assertThat(vendorService.updateVendorActivationStatus(1L, 2L, true)).isNull();
        ArgumentCaptor<Vendor> vendorCaptor = ArgumentCaptor.forClass(Vendor.class);
        verify(vendorRepository, times(1)).saveAndFlush(vendorCaptor.capture());
        assertThat(vendorCaptor.getValue().getActivated()).isEqualTo(true);
    }

    @Test
    void updateVendorBlockStatusNullBody() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(userRepository.findById(2L)).thenReturn(Optional.of(
                new User().id(2L).role(User.RoleEnum.ADMIN)
        ));
        assertThatThrownBy(() -> vendorService.updateVendorBlockStatus(1L, 2L, null))
                .isInstanceOf(MissingBodyException.class)
                .hasMessage("Body is missing");
    }

    @Test
    void updateVendorBlockStatus() throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(userRepository.findById(2L)).thenReturn(Optional.of(
                new User().id(2L).role(User.RoleEnum.ADMIN)
        ));
        assertThat(vendorService.updateVendorBlockStatus(1L, 2L, true)).isNull();
        ArgumentCaptor<Vendor> vendorCaptor = ArgumentCaptor.forClass(Vendor.class);
        verify(vendorRepository, times(1)).saveAndFlush(vendorCaptor.capture());
        assertThat(vendorCaptor.getValue().getBlocked()).isEqualTo(true);
    }
}