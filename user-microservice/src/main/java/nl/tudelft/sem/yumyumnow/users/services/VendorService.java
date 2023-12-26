package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.VendorRepository;
import nl.tudelft.sem.yumyumnow.users.validators.VendorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    private final VendorBuilder vendorBuilder;
    private final VendorValidator vendorValidator;

    @Autowired
    public VendorService(VendorRepository vendorRepository, VendorBuilder vendorBuilder, VendorValidator vendorValidator) {
        this.vendorRepository = vendorRepository;
        this.vendorBuilder = vendorBuilder;
        this.vendorValidator = vendorValidator;
    }

    public Vendor createVendor(VendorRequestModel vendorRequestModel)
            throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException {
        // TODO: implement
        return null;
    }
}
