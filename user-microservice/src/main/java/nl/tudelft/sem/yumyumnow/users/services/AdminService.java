package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (adminRequestModel.getName() == null || adminRequestModel.getSurname() == null || adminRequestModel.getEmail() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Admin admin = new Admin();
        admin.setBlocked(false);
        admin.setName(adminRequestModel.getName());
        admin.setSurname(adminRequestModel.getSurname());
        admin.setEmail(adminRequestModel.getEmail());
        admin.setRole(User.RoleEnum.ADMIN);

        if (adminRepository.findByEmail(adminRequestModel.getEmail()).isPresent())
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        Admin response = adminRepository.saveAndFlush(admin);
        admin.setId(response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }

    public ResponseEntity<Admin> getAdminById(Long adminId){
        Optional<Admin> admin = adminRepository.findById(adminId);

        if (admin.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return ResponseEntity.status(HttpStatus.OK).body(admin.get());
    }

    public ResponseEntity<Void> deleteAdminById(Long adminId){
        try {
            adminRepository.deleteById(adminId);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Void> updateAdminById(Long adminId, AdminRequestModel adminRequestModel){
        Optional<Admin> admin = adminRepository.findById(adminId);

        if (admin.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (adminRequestModel == null || adminRequestModel.getEmail() == null
            || adminRequestModel.getName() == null || adminRequestModel.getSurname() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Admin adminReal = admin.get();
        adminReal.setEmail(adminRequestModel.getEmail());
        adminReal.setName(adminRequestModel.getName());
        adminReal.setSurname(adminRequestModel.getSurname());
        adminRepository.saveAndFlush(adminReal);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
