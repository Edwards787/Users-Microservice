package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.ValidationApi;
import nl.tudelft.sem.yumyumnow.users.model.Validation;
import nl.tudelft.sem.yumyumnow.users.services.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ValidationController implements ValidationApi {
    private final transient VendorService vendorService;

    @Autowired
    public ValidationController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @Override
    public ResponseEntity<List<Validation>> getAllValidations() {
        return ResponseEntity.ok(vendorService.getAllValidations());
    }
}
