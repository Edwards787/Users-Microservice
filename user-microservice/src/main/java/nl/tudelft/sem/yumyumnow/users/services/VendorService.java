package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.VendorBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.VendorRepository;
import nl.tudelft.sem.yumyumnow.users.validators.VendorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    private final VendorBuilder vendorBuilder;
    private final VendorValidator vendorValidator;
    private final UserRepository userRepository;

    @Autowired
    public VendorService(VendorRepository vendorRepository, VendorBuilder vendorBuilder, VendorValidator vendorValidator, UserRepository userRepository) {
        this.vendorRepository = vendorRepository;
        this.vendorBuilder = vendorBuilder;
        this.vendorValidator = vendorValidator;
        this.userRepository = userRepository;
    }

    public Vendor createVendor(VendorRequestModel vendorRequestModel)
            throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException {

        vendorValidator.validate(vendorRequestModel);
        Vendor dummy = new Vendor();
        Vendor vendor = vendorBuilder.build(dummy, vendorRequestModel);

        if (vendorRepository.findByEmail(vendor.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException(vendor.getEmail());
        }

        Vendor response = vendorRepository.saveAndFlush(vendor);
        vendor.setId(response.getId());

        return vendor;
    }

    public Vendor getVendorById(Long vendorId) throws UserNotFoundException {
        Optional<Vendor> vendor = vendorRepository.findById(vendorId);
        if (vendor.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            return vendor.get();
        }
    }


    public Void deleteVendor(Long userid1, Long userid2) throws UserNotFoundException, NotAuthorizedException {
        Optional<Vendor> vendor = vendorRepository.findById(userid1);
        if (vendor.isEmpty()) {
            throw new UserNotFoundException("User not found for id: " + userid1);
        }
        if (userid1.equals(userid2)) {
            vendorRepository.deleteById(userid1);
        } else {
            Optional<User> requester = userRepository.findById(userid2);
            if (requester.isEmpty()) {
                throw new UserNotFoundException("User not found for id: " + userid2);
            }
            if (requester.get().getRole().equals(User.RoleEnum.ADMIN)) {
                vendorRepository.deleteById(userid1);
            } else {
                throw new NotAuthorizedException("User with id " + userid2 + " not authorized to delete vendor with id " + userid1);
            }
        }
        return null;
    }
}
