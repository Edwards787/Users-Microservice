package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import org.springframework.stereotype.Component;

@Component
public class VendorFactory implements UserFactory<Vendor, VendorRequestModel> {
    /**
     * Builds a vendor from a vendor request model.
     *
     * @param vendorRequestModel the vendor request model
     * @return the built vendor
     */
    public Vendor build(VendorRequestModel vendorRequestModel) {
        Vendor vendor = new Vendor();

        vendor.setName(vendorRequestModel.getName());
        vendor.setSurname(vendorRequestModel.getSurname());
        vendor.setEmail(vendorRequestModel.getEmail());
        vendor.setRole(User.RoleEnum.VENDOR);
        vendor.setAddress(vendorRequestModel.getAddress());
        vendor.setOpeningTimes(vendorRequestModel.getOpeningTimes());
        vendor.setClosingTimes(vendorRequestModel.getClosingTimes());
        vendor.setActivated(false);
        vendor.setBlocked(false);

        return vendor;
    }
}
