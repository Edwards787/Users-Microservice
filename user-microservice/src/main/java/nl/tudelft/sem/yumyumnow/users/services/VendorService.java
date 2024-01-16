package nl.tudelft.sem.yumyumnow.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.factories.VendorFactory;
import nl.tudelft.sem.yumyumnow.users.model.Validation;
import nl.tudelft.sem.yumyumnow.users.model.ValidationRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.ValidationRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.VendorRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.Validator;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidatorConstructor;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationData;
import nl.tudelft.sem.yumyumnow.users.validation.objects.authorization.AuthorizationUserAndAdminStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorService {

    private final transient VendorRepository vendorRepository;
    private final transient UserRepository userRepository;
    private final transient VendorFactory vendorFactory;
    private final transient ValidationRepository validationRepository;
    private final transient ValidatorConstructor validatorConstructor;

    /**
     * Creates an instance of the VendorService.
     *
     * @param vendorRepository   vendor repository
     * @param userRepository     user repository
     * @param vendorFactory      vendor factory object
     * @param validationRepository validation repository
     */
    @Autowired
    public VendorService(VendorRepository vendorRepository,
                         UserRepository userRepository,
                         VendorFactory vendorFactory,
                         ValidationRepository validationRepository) {
        this.vendorRepository = vendorRepository;
        this.vendorFactory = vendorFactory;
        this.userRepository = userRepository;
        this.validatorConstructor = new ValidatorConstructor();
        this.validationRepository = validationRepository;
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

        Vendor vendor = vendorFactory.build(vendorRequestModel);

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
    public void updateVendor(Long userId1, Long userId2, VendorRequestModel vendorRequestModel)
            throws UserNotFoundException, NotAuthorizedException, InvalidFieldException,
        MissingBodyException {
        Vendor vendor = getVendorById(userId1);

        Validator validator = validatorConstructor.vendorWithAuthorization();
        validator.handle(new ValidationObject(vendorRequestModel,
                new AuthorizationData(userId1, userId2, userRepository,
                        new AuthorizationUserAndAdminStrategy())));

        vendor.setName(vendorRequestModel.getName());
        vendor.setSurname(vendorRequestModel.getSurname());
        vendor.setEmail(vendorRequestModel.getEmail());
        vendor.setOpeningTimes(vendorRequestModel.getOpeningTimes());
        vendor.setClosingTimes(vendorRequestModel.getClosingTimes());
        vendor.setAddress(vendorRequestModel.getAddress());

        vendorRepository.saveAndFlush(vendor);
    }

    private double distance(double lat1,
                            double lat2, double lon1,
                            double lon2) {
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371; // Radius of earth in kilometers.
        return (c * r);
    }

    /**
     * Gets a list of vendors within a radius of a given point.
     *
     * @param radius    the radius in km
     * @param latitude  the latitude
     * @param longitude the longitude
     * @return the list of vendors
     * @throws InvalidFieldException if the radius, latitude or longitude is null
     */
    public List<Vendor> getVendorsByRadius(Long radius, Float latitude, Float longitude)
            throws InvalidFieldException, ContentNotFoundException {
        if (radius == null || radius < 0 || radius > 1000) {
            throw new InvalidFieldException("radius", String.valueOf(radius));
        }
        if (latitude == null || latitude < -90 || latitude > 90) {
            throw new InvalidFieldException("latitude", String.valueOf(latitude));
        }
        if (longitude == null || longitude < -180 || longitude > 180) {
            throw new InvalidFieldException("longitude", String.valueOf(longitude));
        }
        List<Vendor> result = new ArrayList<>();
        List<Vendor> allVendor = vendorRepository.findAll();
        for (Vendor vendor : allVendor) {
            Long curDist = Math.round(distance(
                    latitude,
                    vendor.getAddress().getCoordinates().getLat(),
                    longitude,
                    vendor.getAddress().getCoordinates().getLong()
            ));
            if (curDist <= radius) {
                result.add(vendor);
            }
        }
        if (result.isEmpty()) {
            throw new ContentNotFoundException("No vendors found in the given radius");
        }
        return result;
    }

    /**
     * Uploads a validation for a vendor.
     *
     * @param userId                 the vendor id
     * @param validationRequestModel the validation request model
     * @return null
     * @throws UserNotFoundException if the vendor is not found, specifying the id
     * @throws InvalidFieldException if the request model contains invalid fields, specifying the field and the value
     * @throws MissingBodyException  if the request model is null
     */
    public void uploadValidationByVendor(Long userId, ValidationRequestModel validationRequestModel)
            throws UserNotFoundException, InvalidFieldException, MissingBodyException {
        if (userId == null) {
            throw new InvalidFieldException("userId", String.valueOf(userId));
        }

        Optional<Vendor> vendor = vendorRepository.findById(userId);
        if (vendor.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        if (validationRequestModel == null) {
            throw new MissingBodyException();
        }

        if (validationRequestModel.getDocument().isEmpty()) {
            throw new InvalidFieldException("document", validationRequestModel.getDocument());
        }

        Validation validation = new Validation();
        validation.setVendorId(userId);
        validation.setDocument(validationRequestModel.getDocument());
        validationRepository.saveAndFlush(validation);
    }

    /**
     * Gets a validation for a vendor.
     *
     * @param userId the vendor id
     * @return the validation
     * @throws UserNotFoundException   if the vendor is not found, specifying the id
     * @throws ContentNotFoundException if the validation is not found
     */
    public Validation getValidationByVendor(Long userId) throws UserNotFoundException, ContentNotFoundException {
        if (userId == null) {
            throw new UserNotFoundException(userId);
        }
        if (!vendorRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        Optional<Validation> validation = validationRepository.findById(userId);
        if (validation.isEmpty()) {
            throw new ContentNotFoundException("Validation not found");
        }
        return validation.get();
    }

    /**
     * Updates a validation for a vendor.
     *
     * @param userId1                the vendor id
     * @param userId2                the user id requesting the update
     * @param validationRequestModel the validation request model
     * @return null
     * @throws UserNotFoundException  if the vendor or the requester is not found, specifying the id
     * @throws NotAuthorizedException if the requester is not authorized to update the validation
     * @throws InvalidFieldException  if the request model contains invalid fields, specifying the field and the value
     * @throws MissingBodyException   if the request model is null
     */
    public void updateValidationByVendor(Long userId1, Long userId2, ValidationRequestModel validationRequestModel)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException, InvalidFieldException {
        new AuthorizationUserAndAdminStrategy().authorize(userId1, userId2, userRepository);
        if (validationRequestModel == null) {
            throw new MissingBodyException();
        }
        if (validationRequestModel.getDocument().isEmpty()) {
            throw new InvalidFieldException("document", validationRequestModel.getDocument());
        }
        Validation validation = new Validation();
        validation.setVendorId(userId1);
        validation.setDocument(validationRequestModel.getDocument());
        validationRepository.saveAndFlush(validation);
    }

    /**
     * Deletes a validation for a vendor.
     *
     * @param userId1 the vendor id
     * @param userId2 the user id requesting the deletion
     * @return null
     * @throws UserNotFoundException  if the vendor or the requester is not found, specifying the id
     * @throws NotAuthorizedException if the requester is not authorized to delete the validation
     */
    public Void deleteValidationByVendor(Long userId1, Long userId2)
            throws UserNotFoundException, NotAuthorizedException {
        new AuthorizationUserAndAdminStrategy().authorize(userId1, userId2, userRepository);
        validationRepository.deleteById(userId1);
        return null;
    }

    public List<Validation> getAllValidations() {
        return validationRepository.findAll();
    }

    /**
     * Updates the activation status of a vendor.
     *
     * @param userId1 the vendor id
     * @param userId2 the user id requesting the update
     * @param body    the new activation status
     * @return null
     * @throws UserNotFoundException  if the vendor or the requester is not found, specifying the id
     * @throws NotAuthorizedException if the requester is not authorized to update the activation status
     * @throws MissingBodyException   if the request model is null
     */
    public void updateVendorActivationStatus(Long userId1, Long userId2, Boolean body)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        new AuthorizationAdminOnlyStrategy().authorize(userId1, userId2, userRepository);
        if (body == null) {
            throw new MissingBodyException();
        }
        Vendor vendor = vendorRepository.findById(userId1).get();
        vendor.setActivated(body);
        vendorRepository.saveAndFlush(vendor);
    }

    /**
     * Updates the block status of a vendor.
     *
     * @param userId1 the vendor id
     * @param userId2 the user id requesting the update
     * @param body    the new block status
     * @return null
     * @throws UserNotFoundException  if the vendor or the requester is not found, specifying the id
     * @throws NotAuthorizedException if the requester is not authorized to update the block status
     * @throws MissingBodyException   if the request model is null
     */
    public void updateVendorBlockStatus(Long userId1, Long userId2, Boolean body)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException {
        new AuthorizationAdminOnlyStrategy().authorize(userId1, userId2, userRepository);
        if (body == null) {
            throw new MissingBodyException();
        }
        Vendor vendor = vendorRepository.findById(userId1).get();
        vendor.setBlocked(body);
        vendorRepository.saveAndFlush(vendor);
    }
}
