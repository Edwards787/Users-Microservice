package nl.tudelft.sem.yumyumnow.users.controllers;

import java.util.List;
import nl.tudelft.sem.yumyumnow.users.api.VendorApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Validation;
import nl.tudelft.sem.yumyumnow.users.model.ValidationRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import nl.tudelft.sem.yumyumnow.users.model.VendorRequestModel;
import nl.tudelft.sem.yumyumnow.users.services.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;


@Controller
public class VendorController implements VendorApi {

    private final transient VendorService vendorService;

    @Autowired
    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @Override
    public ResponseEntity<Vendor> createVendor(VendorRequestModel vendorRequestModel) {
        try {
            return ResponseEntity.ok(vendorService.createVendor(vendorRequestModel));
        } catch (MissingBodyException | InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EmailAlreadyInUseException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UserNotFoundException | NotAuthorizedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Void> deleteVendor(Long userid1, Long userid2) {
        try {
            return ResponseEntity.ok(vendorService.deleteVendor(userid1, userid2));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User with id " + userid2 + " not authorized to delete vendor with id " + userid1);
        } catch (MissingBodyException | InvalidFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Vendor> getVendorById(Long vendorId) {
        try {
            return ResponseEntity.ok(vendorService.getVendorById(vendorId));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        }
    }

    @Override
    public ResponseEntity<List<Vendor>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @Override
    public ResponseEntity<Void> updateVendor(Long userId1, Long userId2, VendorRequestModel vendorRequestModel) {
        try {
            return ResponseEntity.ok(vendorService.updateVendor(userId1, userId2, vendorRequestModel));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User with id " + userId2 + " not authorized to update vendor with id " + userId1);
        } catch (InvalidFieldException | MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<Vendor>> getVendorsByRadius(Long radius, Float latitude, Float longitude) {
        try {
            List<Vendor> result = vendorService.getVendorsByRadius(radius, latitude, longitude);
            return ResponseEntity.ok(result);
        } catch (InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ContentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> uploadValidationByVendor(Long userId, ValidationRequestModel validationRequestModel) {
        try {
            return ResponseEntity.ok(vendorService.uploadValidationByVendor(userId, validationRequestModel));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        } catch (InvalidFieldException | MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Validation> getValidationByVendor(Long userId) {
        try {
            return ResponseEntity.ok(vendorService.getValidationByVendor(userId));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        } catch (ContentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> updateValidationByVendor(Long userId1, Long userId2,
                                                         ValidationRequestModel validationRequestModel) {
        try {
            return ResponseEntity.ok(vendorService.updateValidationByVendor(userId1, userId2, validationRequestModel));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User with id " + userId2 + " not authorized to update validation with id " + userId1);
        } catch (InvalidFieldException | MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> deleteValidationByVendor(Long userId1, Long userId2) {
        try {
            return ResponseEntity.ok(vendorService.deleteValidationByVendor(userId1, userId2));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User with id " + userId2 + " not authorized to delete validation with id " + userId1);
        }
    }

    @Override
    public ResponseEntity<Void> updateVendorActivationStatus(Long userId1, Long userId2, Boolean body) {
        try {
            return ResponseEntity.ok(vendorService.updateVendorActivationStatus(userId1, userId2, body));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User with id " + userId2 + " not authorized to update activation status of vendor with id " + userId1);
        } catch (MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> updateVendorBlockStatus(Long userId1, Long userId2, Boolean body) {
        try {
            return ResponseEntity.ok(vendorService.updateVendorBlockStatus(userId1, userId2, body));
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User with id " + userId2 + " not authorized to update block status of vendor with id " + userId1);
        } catch (MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
