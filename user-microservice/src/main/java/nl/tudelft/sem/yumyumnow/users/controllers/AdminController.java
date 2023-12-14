package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.AdminApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Controller
public class AdminController implements AdminApi {

    private final AdminService adminService;
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    public ResponseEntity<Admin> getAdminById(Long adminId){
        return adminService.getAdminById(adminId);
    }

    @Override
    public ResponseEntity<Void> deleteAdmin(Long userId1, Long userId2){
        return adminService.deleteAdminById(userId1, userId2);
    }

    @Override
    public ResponseEntity<Void> updateAdmin(Long userId1, Long userId2, AdminRequestModel adminRequestModel){
        return adminService.updateAdminById(userId1, userId2, adminRequestModel);
    }

    @Override
    public ResponseEntity<List<Long>> getRefundRequests(Long userId){
        return adminService.getRefundRequests(userId);
    }

    public ResponseEntity<Admin> createAdmin(AdminRequestModel adminRequestModel){
        ResponseEntity<Admin> responseEntity;
        try {
            responseEntity = adminService.createAdmin(adminRequestModel);
        }
        catch (StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }
}
