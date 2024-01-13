package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VendorBuilderTest {
    private VendorBuilder vendorBuilder;
    private VendorRequestModel vendorRequestModel;
    private Vendor vendor;

    @BeforeEach
    public void setUp() {
        vendorBuilder = new VendorBuilder();
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
        vendor = new Vendor();
    }

    @Test
    public void testEmptyInitialVendor() {
        Vendor resultVendor = vendorBuilder.build(vendor, vendorRequestModel);
        assertThat(resultVendor.getAddress()).isEqualTo(vendorRequestModel.getAddress());
        assertThat(resultVendor.getOpeningTimes()).isEqualTo(vendorRequestModel.getOpeningTimes());
        assertThat(resultVendor.getClosingTimes()).isEqualTo(vendorRequestModel.getClosingTimes());
        assertThat(resultVendor.getEmail()).isEqualTo(vendorRequestModel.getEmail());
        assertThat(resultVendor.getName()).isEqualTo(vendorRequestModel.getName());
        assertThat(resultVendor.getSurname()).isEqualTo(vendorRequestModel.getSurname());
        assertThat(resultVendor.getRole()).isEqualTo(Vendor.RoleEnum.VENDOR);
        assertThat(resultVendor.getBlocked()).isEqualTo(false);
        assertThat(resultVendor.getActivated()).isEqualTo(false);
    }

    @Test
    public void testFilledInitialVendor() {
        vendor.setEmail("newEmail@email.com");
        vendor.setBlocked(true);
        vendor.setActivated(true);
        vendor.setOpeningTimes("");

        Vendor resultVendor = vendorBuilder.build(vendor, vendorRequestModel);
        assertThat(resultVendor.getAddress()).isEqualTo(vendorRequestModel.getAddress());
        assertThat(resultVendor.getOpeningTimes()).isEqualTo(vendorRequestModel.getOpeningTimes());
        assertThat(resultVendor.getClosingTimes()).isEqualTo(vendorRequestModel.getClosingTimes());
        assertThat(resultVendor.getEmail()).isEqualTo(vendorRequestModel.getEmail());
        assertThat(resultVendor.getName()).isEqualTo(vendorRequestModel.getName());
        assertThat(resultVendor.getSurname()).isEqualTo(vendorRequestModel.getSurname());
        assertThat(resultVendor.getRole()).isEqualTo(Vendor.RoleEnum.VENDOR);
        assertThat(resultVendor.getBlocked()).isEqualTo(true);
        assertThat(resultVendor.getActivated()).isEqualTo(true);
    }
}
