package nl.tudelft.sem.yumyumnow.users.controllers;

import java.util.List;
import nl.tudelft.sem.yumyumnow.users.api.AdminApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InitialAdminException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
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

    private final transient AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public ResponseEntity<Admin> getAdminById(Long adminId) {
        try {
            Admin admin = adminService.getAdminById(adminId);
            return ResponseEntity.ok(admin);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found");
        }

    }

    @Override
    public ResponseEntity<Admin> createAdmin(AdminRequestModel adminRequestModel) {
        try {
            return ResponseEntity.ok(adminService.createAdmin(adminRequestModel));
        } catch (MissingBodyException | InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EmailAlreadyInUseException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UserNotFoundException | NotAuthorizedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAdmin(Long userId1, Long userId2) {
        try {
            adminService.deleteAdminById(userId1, userId2);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found");
        } catch (InitialAdminException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Initial admin can not be deleted");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        } catch (MissingBodyException | InvalidFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Void> updateAdmin(Long userId1, Long userId2, AdminRequestModel adminRequestModel) {
        try {
            adminService.updateAdminById(userId1, userId2, adminRequestModel);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        } catch (InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body not present");
        }
    }

    @Override
    public ResponseEntity<List<Long>> getRefundRequests(Long userId) {
        return adminService.getRefundRequests(userId);
    }
}
