package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.swing.text.html.Option;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    @Autowired
    public AdminService(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public Admin getAdminById(Long adminId){
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isEmpty())
            throw new StatusException(HttpStatus.NOT_FOUND, "Admin not found");
        else return admin.get();
    }

    public Admin createAdmin(AdminRequestModel adminRequestModel) {
        if (adminRequestModel == null)
           throw new StatusException(HttpStatus.BAD_REQUEST, "No admin request model present");

        if (adminRequestModel.getName() == null || adminRequestModel.getName().equals(""))
            throw new StatusException(HttpStatus.BAD_REQUEST, "No name provided");

        if (adminRequestModel.getSurname() == null || adminRequestModel.getSurname().equals(""))
            throw new StatusException(HttpStatus.BAD_REQUEST, "No surname provided");

        if (adminRequestModel.getEmail() == null || adminRequestModel.getEmail().equals(""))
            throw new StatusException(HttpStatus.BAD_REQUEST, "No email provided");

        if (adminRepository.findByEmail(adminRequestModel.getEmail()).isPresent())
            throw new StatusException(HttpStatus.CONFLICT, "User with this email address already exists");

        Admin admin = new Admin();
        admin.setBlocked(false);
        admin.setName(adminRequestModel.getName());
        admin.setSurname(adminRequestModel.getSurname());
        admin.setEmail(adminRequestModel.getEmail());
        admin.setRole(User.RoleEnum.ADMIN);

        Admin response = adminRepository.saveAndFlush(admin);
        admin.setId(response.getId());

        return admin;
    }

    public void deleteAdminById(Long adminId1, Long adminId2){
        if (adminId1 == 0)
            throw new StatusException(HttpStatus.valueOf(403), "Initial admin cannot be deleted");
        try{
            Admin admin = getAdminById(adminId2);
        } catch (StatusException exception){
            throw new StatusException(HttpStatus.valueOf(403), "Unauthorized");
        }
        try {
            adminRepository.deleteById(adminId1);
        } catch (EmptyResultDataAccessException e) {
            throw new StatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    public void updateAdminById(Long adminId1, Long adminId2, AdminRequestModel adminRequestModel){
        Admin admin;
        if (adminRequestModel == null)
            throw new StatusException(HttpStatus.BAD_REQUEST, "No admin update model present");
        if (adminRequestModel.getName() == null || adminRequestModel.getName().equals(""))
            throw new StatusException(HttpStatus.BAD_REQUEST, "No name provided");
        if (adminRequestModel.getSurname() == null || adminRequestModel.getSurname().equals(""))
            throw new StatusException(HttpStatus.BAD_REQUEST, "No surname provided");
        if (adminRequestModel.getEmail() == null || adminRequestModel.getEmail().equals(""))
            throw new StatusException(HttpStatus.BAD_REQUEST, "No email provided");

        try{
            admin = getAdminById(adminId2);
        } catch (StatusException exception){
            throw new StatusException(HttpStatus.valueOf(403), "Unauthorized");
        }

        try{
            admin = getAdminById(adminId1);
        } catch (StatusException exception){
            throw new StatusException(HttpStatus.valueOf(404), "Admin not found");
        }

        admin.setEmail(adminRequestModel.getEmail());
        admin.setName(adminRequestModel.getName());
        admin.setSurname(adminRequestModel.getSurname());
        adminRepository.saveAndFlush(admin);
    }

    public ResponseEntity<List<Long>> getRefundRequests(Long userId) {
        return ResponseEntity.ok(new ArrayList<>());
    }
}