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
            vendorService.deleteVendor(userid1, userid2);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.customMessage(userid2, "delete", userid1));
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
            vendorService.updateVendor(userId1, userId2, vendorRequestModel);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.customMessage(userId2, "update", userId1));
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
            vendorService.uploadValidationByVendor(userId, validationRequestModel);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        } catch (InvalidFieldException | MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Validation> getValidationByVendor(Long userId) {
        try {
            Validation validation = vendorService.getValidationByVendor(userId);
            return ResponseEntity.ok(validation);
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
            vendorService.updateValidationByVendor(userId1, userId2, validationRequestModel);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.customMessage(userId2, "update validation for", userId1));
        } catch (InvalidFieldException | MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> deleteValidationByVendor(Long userId1, Long userId2) {
        try {
            vendorService.deleteValidationByVendor(userId1, userId2);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.customMessage(userId2, "delete validation for", userId1));
        }
    }

    @Override
    public ResponseEntity<Void> updateVendorActivationStatus(Long userId1, Long userId2, Boolean body) {
        try {
            vendorService.updateVendorActivationStatus(userId1, userId2, body);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.customMessage(userId2, "update activation status for", userId1));
        } catch (MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> updateVendorBlockStatus(Long userId1, Long userId2, Boolean body) {
        try {
            vendorService.updateVendorBlockStatus(userId1, userId2, body);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    e.customMessage(userId2, "update block status for", userId1));
        } catch (MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
