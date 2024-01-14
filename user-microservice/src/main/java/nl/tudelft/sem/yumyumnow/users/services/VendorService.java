package nl.tudelft.sem.yumyumnow.users.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.builders.VendorFactory;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.VendorRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.Validator;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidatorConstructor;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationData;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationUserAndAdminStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorService {

    private final transient VendorRepository vendorRepository;
    private final transient UserRepository userRepository;
    private final transient VendorFactory vendorFactory;
    private final transient ValidatorConstructor validatorConstructor;

    /**
     * Creates an instance of the VendorService.
     *
     * @param vendorRepository     the vendor repository
     * @param vendorFactory        the vendor builder
     */
    @Autowired
    public VendorService(VendorRepository vendorRepository,
                         UserRepository userRepository,
                         VendorFactory vendorFactory) {
        this.vendorRepository = vendorRepository;
        this.vendorFactory = vendorFactory;
        this.userRepository = userRepository;
        this.validatorConstructor = new ValidatorConstructor();
    }

    /**
     * Creates a vendor.
     *
     * @param vendorRequestModel the vendor request model
     * @return the created vendor
     * @throws InvalidFieldException      if the request model contains invalid fields, specifying the field and the value
     * @throws MissingBodyException       if the request model is null
     * @throws EmailAlreadyInUseException if the email is already in use, specifying the email
     */
    public Vendor createVendor(VendorRequestModel vendorRequestModel)
        throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException,
        UserNotFoundException, NotAuthorizedException {

        Validator validator = validatorConstructor.vendorWithoutAuthorization();
        validator.handle(new ValidationObject(vendorRequestModel, null));

        Vendor dummy = new Vendor();
        Vendor vendor = vendorFactory.build(dummy, vendorRequestModel);

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
     * @param userid1 the vendor id being deleted
     * @param userid2 the user id requesting the deletion
     * @return null
     * @throws UserNotFoundException  if the vendor or the requester is not found, specifying the id
     * @throws NotAuthorizedException if the requester is not authorized to delete the vendor
     */
    public Void deleteVendor(Long userid1, Long userid2)
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(new ValidationObject(null, new AuthorizationData(userid1,
            userid2, userRepository, new AuthorizationUserAndAdminStrategy())));

        vendorRepository.deleteById(userid1);
        return null;
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    /**
     * Updates a vendor.
     *
     * @param userId1            the vendor id being updated
     * @param userId2            the user id requesting the update
     * @param vendorRequestModel the vendor request model
     * @return null
     * @throws UserNotFoundException  if the vendor or the requester is not found, specifying the id
     * @throws NotAuthorizedException if the requester is not authorized to update the vendor
     * @throws InvalidFieldException  if the request model contains invalid fields, specifying the field and the value
     * @throws MissingBodyException   if the request model is null
     */
    public Void updateVendor(Long userId1, Long userId2, VendorRequestModel vendorRequestModel)
            throws UserNotFoundException, NotAuthorizedException, InvalidFieldException, MissingBodyException {
        Optional<Vendor> vendor = vendorRepository.findById(userId1);

        Validator validator = validatorConstructor.vendorWithAuthorization();
        validator.handle(new ValidationObject(vendorRequestModel,
            new AuthorizationData(userId1, userId2, userRepository,
                new AuthorizationUserAndAdminStrategy())));

        Vendor updated = vendorFactory.build(vendor.get(), vendorRequestModel);
        vendorRepository.saveAndFlush(updated);
        return null;
    }
}
