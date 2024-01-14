package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.VendorFactory;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class VendorServiceTest {
    transient VendorRepository vendorRepository;
    transient UserRepository userRepository;
    transient VendorFactory vendorFactory;
    transient VendorService vendorService;
    transient Vendor vendor;
    transient VendorRequestModel vendorRequestModel;
    @BeforeEach
    public void setUp() {
        vendorRepository = mock(VendorRepository.class);
        vendorFactory = new VendorFactory();
        userRepository = mock(UserRepository.class);
        vendorService = new VendorService(vendorRepository, userRepository, vendorFactory);

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
        when(vendorRepository.saveAndFlush(vendorFactory.build(new Vendor(), vendorRequestModel))).thenReturn(vendor);
        assertThat(vendorService.createVendor(vendorRequestModel)).isEqualTo(vendor);
        verify(vendorRepository, times(1)).saveAndFlush(any());
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
}