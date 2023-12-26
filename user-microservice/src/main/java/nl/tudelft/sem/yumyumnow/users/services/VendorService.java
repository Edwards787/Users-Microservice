package nl.tudelft.sem.yumyumnow.users.services;

import java.util.Optional;
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

@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    private final VendorBuilder vendorBuilder;
    private final VendorValidator vendorValidator;
    private final UserRepository userRepository;

    /**
     * Creates an instance of the VendorService.
     *
     * @param vendorRepository the vendor repository
     * @param vendorBuilder the vendor builder
     * @param vendorValidator the vendor validator
     * @param userRepository the user repository
     */
    @Autowired
    public VendorService(VendorRepository vendorRepository,
                         VendorBuilder vendorBuilder,
                         VendorValidator vendorValidator,
                         UserRepository userRepository) {
        this.vendorRepository = vendorRepository;
        this.vendorBuilder = vendorBuilder;
        this.vendorValidator = vendorValidator;
        this.userRepository = userRepository;
    }

    /**
     * Creates a vendor.
     *
     * @param vendorRequestModel the vendor request model
     * @return the created vendor
     * @throws InvalidFieldException if the request model contains invalid fields, specifying the field and the value
     * @throws MissingBodyException if the request model is null
     * @throws EmailAlreadyInUseException if the email is already in use, specifying the email
     */
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

    /**
     * Gets a vendor by id.
     *
     * @param vendorId the vendor id
     * @return the vendor
     * @throws UserNotFoundException if the vendor is not found, specifying the id
     */
    public Vendor getVendorById(Long vendorId) throws UserNotFoundException {
        Optional<Vendor> vendor = vendorRepository.findById(vendorId);
        if (vendor.isEmpty()) {
            throw new UserNotFoundException(vendorId);
        } else {
            return vendor.get();
        }
    }

    /**
     * Deletes a vendor.
     *
     * @param userid1 the vendor id
     * @param userid2 the user id
     * @return null
     * @throws UserNotFoundException if the vendor or the requester is not found, specifying the id
     * @throws NotAuthorizedException if the requester is not authorized to delete the vendor
     */
    public Void deleteVendor(Long userid1, Long userid2) throws UserNotFoundException, NotAuthorizedException {
        Optional<Vendor> vendor = vendorRepository.findById(userid1);
        if (vendor.isEmpty()) {
            throw new UserNotFoundException(userid1);
        }
        if (userid1.equals(userid2)) {
            vendorRepository.deleteById(userid1);
        } else {
            Optional<User> requester = userRepository.findById(userid2);
            if (requester.isEmpty()) {
                throw new UserNotFoundException(userid2);
            }
            if (requester.get().getRole().equals(User.RoleEnum.ADMIN)) {
                vendorRepository.deleteById(userid1);
            } else {
                throw new NotAuthorizedException();
            }
        }
        return null;
    }
}
