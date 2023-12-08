package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.AdminApi;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class AdminController implements AdminApi {

    private final AdminService adminService;
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public ResponseEntity<Admin> createAdmin(AdminRequestModel adminRequestModel){
        return adminService.createAdmin(adminRequestModel);
    }

    @Override
    public ResponseEntity<Admin> getAdminById(Long adminId){
        return adminService.getAdminById(adminId);
    }

    @Override
    public ResponseEntity<Void> deleteAdmin(Long adminId){
        return adminService.deleteAdminById(adminId);
    }

    @Override
    public ResponseEntity<Void> updateAdmin(Long userId, AdminRequestModel adminRequestModel){
        return adminService.updateAdminById(userId, adminRequestModel);
    }
}
