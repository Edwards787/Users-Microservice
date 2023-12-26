package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.VendorRepository;
import nl.tudelft.sem.yumyumnow.users.services.VendorService;
import nl.tudelft.sem.yumyumnow.users.validators.AddressValidator;
import nl.tudelft.sem.yumyumnow.users.validators.LocationValidator;
import nl.tudelft.sem.yumyumnow.users.validators.VendorValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class VendorServiceTest {
    private VendorRepository vendorRepository;
    private UserRepository userRepository;
    private VendorBuilder vendorBuilder;
    private VendorService vendorService;
    private Vendor vendor;
    private VendorRequestModel vendorRequestModel;
    @BeforeEach
    public void setUp() {
        vendorRepository = mock(VendorRepository.class);
        userRepository = mock(UserRepository.class);
        vendorBuilder = new VendorBuilder();
        VendorValidator vendorValidator = new VendorValidator(new AddressValidator(new LocationValidator()));
        vendorService = new VendorService(vendorRepository, vendorBuilder, vendorValidator, userRepository);

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
    void createVendor() throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException {
        when(vendorRepository.findByEmail(vendorRequestModel.getEmail())).thenReturn(Optional.empty());
        when(vendorRepository.saveAndFlush(vendorBuilder.build(new Vendor(), vendorRequestModel))).thenReturn(vendor);
        assertThat(vendorService.createVendor(vendorRequestModel)).isEqualTo(vendor);
    }

    @Test
    void createVendorExistingEmail() {
        when(vendorRepository.findByEmail(vendorRequestModel.getEmail())).thenReturn(Optional.of(vendor));
        try{
            vendorService.createVendor(vendorRequestModel);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EmailAlreadyInUseException.class);
            assert e instanceof EmailAlreadyInUseException;
            assertThat(((EmailAlreadyInUseException) e).getEmail()).isEqualTo(vendorRequestModel.getEmail());
            assertThat(e.getMessage()).isEqualTo("User with email '" + vendorRequestModel.getEmail()  + "' already exists");
        }
    }

    @Test
    void createVendorMissingBody() {
        try{
            vendorService.createVendor(null);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(MissingBodyException.class);
        }
    }

    @Test
    void createVendorInvalidField() {
        when(vendorRepository.findByEmail(vendorRequestModel.getEmail())).thenReturn(Optional.empty());
        vendorRequestModel.setEmail("invalidEmail");
        try{
            vendorService.createVendor(vendorRequestModel);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(InvalidFieldException.class);
            assert e instanceof InvalidFieldException;
            assertThat(((InvalidFieldException) e).getFieldName()).isEqualTo("email");
            assertThat(e.getMessage()).isEqualTo("Field 'email' with value 'invalidEmail' is invalid");
        }
    }

    @Test
    void getVendorById() throws UserNotFoundException {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        assertThat(vendorService.getVendorById(1L)).isEqualTo(vendor);
    }

    @Test
    void getVendorByIdNotFound() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.empty());
        try{
            vendorService.getVendorById(1L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UserNotFoundException.class);
            assert e instanceof UserNotFoundException;
            assertThat(((UserNotFoundException) e).getTargetId()).isEqualTo(1L);
        }
    }

    @Test
    void deleteVendorNotFound() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.empty());
        try{
            vendorService.deleteVendor(1L, 1L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UserNotFoundException.class);
            assert e instanceof UserNotFoundException;
            assertThat(((UserNotFoundException) e).getTargetId()).isEqualTo(1L);
        }
    }

    @Test
    void deleteRequesterNotFound() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        try{
            vendorService.deleteVendor(1L, 2L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(UserNotFoundException.class);
            assert e instanceof UserNotFoundException;
            assertThat(((UserNotFoundException) e).getTargetId()).isEqualTo(2L);
        }
    }

    @Test
    void deleteVendorSameRequester() throws UserNotFoundException, NotAuthorizedException {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        vendorService.deleteVendor(1L, 1L);
        verify(vendorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteVendorDifferentRequesterAuthorized() throws UserNotFoundException, NotAuthorizedException {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()
                .id(2L)
                .role(nl.tudelft.sem.yumyumnow.users.model.User.RoleEnum.ADMIN)));
        vendorService.deleteVendor(1L, 2L);
        verify(vendorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteVendorDifferentRequesterNotAuthorized() {
        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()
                .id(2L)
                .role(User.RoleEnum.VENDOR)));
        try{
            vendorService.deleteVendor(1L, 2L);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(NotAuthorizedException.class);
            assertThat(e.getMessage()).isEqualTo("Not authorized"); // custom message not parsed yet
        }
    }
}