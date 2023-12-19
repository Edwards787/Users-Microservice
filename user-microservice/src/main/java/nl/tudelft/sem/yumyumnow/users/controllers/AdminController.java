package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.AdminApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class AdminController implements AdminApi {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public ResponseEntity<Admin> getAdminById(Long adminId) {
        try {
            return adminService.getAdminById(adminId);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Admin> createAdmin(AdminRequestModel adminRequestModel) {
        try {
            return adminService.createAdmin(adminRequestModel);
        } catch (MissingBodyException | InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EmailAlreadyInUseException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}





