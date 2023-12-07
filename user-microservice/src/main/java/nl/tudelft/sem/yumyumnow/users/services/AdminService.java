package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.net.URI;
import java.util.Optional;

@Service
public class AdminService {
    AdminRepository adminRepository;
    @Autowired
    public AdminService(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public ResponseEntity<Admin> createAdmin(AdminRequestModel adminRequestModel){
        if (adminRequestModel == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        if (adminRequestModel.getName() == null || adminRequestModel.getSurname() == null || adminRequestModel.getEmail() == null)
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        Admin admin = new Admin();
        admin.setBlocked(false);
        admin.setName(adminRequestModel.getName());
        admin.setSurname(adminRequestModel.getSurname());
        admin.setEmail(adminRequestModel.getEmail());
        admin.setRole(User.RoleEnum.ADMIN);

        if (!adminRepository.findByEmail(adminRequestModel.getEmail()).isEmpty())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        Admin response = adminRepository.saveAndFlush(admin);
        admin.setId(response.getId());

        return ResponseEntity.created(URI.create("https://localhost:8081/admin/" + admin.getId())).build();
    }

    public Admin getAdminById(Long adminId){
        Optional<Admin> admin = adminRepository.findById(adminId);
        return admin.get();
    }

    public void deleteAdminById(Long adminId){
        adminRepository.deleteById(adminId);
    }

}
