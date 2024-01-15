package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.services.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationControllerTest {

    transient ValidationController validationController;
    transient VendorService vendorService;
    transient Validation validation;
    @BeforeEach
    public void setUp() {
        vendorService = mock(VendorService.class);
        validationController = new ValidationController(vendorService);
        validation = new Validation();
        validation.setVendorId(1L);
        validation.setDocument("document");
    }

    @Test
    void getAllValidations() {
        when(vendorService.getAllValidations()).thenReturn(List.of(validation));
        ResponseEntity<List<Validation>> res = validationController.getAllValidations();
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isEqualTo(List.of(validation));
    }
}