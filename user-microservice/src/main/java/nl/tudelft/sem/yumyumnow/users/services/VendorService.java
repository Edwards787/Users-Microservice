package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.model.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class VendorService {

    private final VendorRepository vendorRepository;
    @Autowired
    public VendorService(VendorRepository vendorRepository){
        this.vendorRepository = vendorRepository;
    }
}
