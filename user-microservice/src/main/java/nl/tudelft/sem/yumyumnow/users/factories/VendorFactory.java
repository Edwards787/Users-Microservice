package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import org.springframework.stereotype.Service;

@Service
public class VendorFactory extends UserFactory {
    /**
     * Builds a vendor from a vendor request model.
     *
     * @param vendorRequestModel the vendor request model
     * @return the built vendor
     */
    public Vendor build(VendorRequestModel vendorRequestModel) {
        Vendor vendor = (Vendor) super.build(vendorRequestModel);

        vendor.setRole(User.RoleEnum.VENDOR);
        vendor.setAddress(vendorRequestModel.getAddress());
        vendor.setOpeningTimes(vendorRequestModel.getOpeningTimes());
        vendor.setClosingTimes(vendorRequestModel.getClosingTimes());
        vendor.setActivated(false);

        return vendor;
    }
}
