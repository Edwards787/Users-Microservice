package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.services.UserService;
import nl.tudelft.sem.yumyumnow.users.services.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class VendorController {

    private final VendorService vendorService;
    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

}
