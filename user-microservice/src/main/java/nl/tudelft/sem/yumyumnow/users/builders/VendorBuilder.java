package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import org.springframework.stereotype.Service;

@Service
public class VendorBuilder extends UserBuilder {
    public Vendor build(Vendor vendor, VendorRequestModel vendorRequestModel) {
        super.build(vendor, vendorRequestModel);
        vendor.setRole(User.RoleEnum.VENDOR);
        vendor.setAddress(vendorRequestModel.getAddress());
        vendor.setOpeningTimes(vendorRequestModel.getOpeningTimes());
        vendor.setClosingTimes(vendorRequestModel.getClosingTimes());
        if (vendor.getActivated() == null) {
            vendor.setActivated(false);
        }
        return vendor;
    }
}
