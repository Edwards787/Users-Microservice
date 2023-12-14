package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.AdminApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;


@Controller
public class AdminController implements AdminApi {

    private final AdminService adminService;
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    public ResponseEntity<Admin> getAdminById(Long adminId){
        Admin admin;
        try {
            admin = adminService.getAdminById(adminId);
        }
        catch (StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return ResponseEntity.ok(admin);
    }

    @Override
    public ResponseEntity<Void> deleteAdmin(Long userId1, Long userId2){
        try{
            adminService.deleteAdminById(userId1, userId2);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> updateAdmin(Long userId1, Long userId2, AdminRequestModel adminRequestModel){
        try{
            adminService.updateAdminById(userId1, userId2, adminRequestModel);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
    }

    public ResponseEntity<Admin> createAdmin(AdminRequestModel adminRequestModel){
        Admin admin;
        try {
            admin = adminService.createAdmin(adminRequestModel);
        }
        catch (StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return ResponseEntity.created(URI.create("")).body(admin);
    }

    @Override
    public ResponseEntity<List<Long>> getRefundRequests(Long userId){
        return adminService.getRefundRequests(userId);
    }
}
