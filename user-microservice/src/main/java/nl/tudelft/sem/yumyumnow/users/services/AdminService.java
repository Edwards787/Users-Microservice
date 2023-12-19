package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.AdminBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.validators.AdminValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminValidator adminValidator;
    private final AdminBuilder adminBuilder;
    @Autowired
    public AdminService(AdminRepository adminRepository, AdminBuilder adminBuilder, AdminValidator adminValidator){
        this.adminRepository = adminRepository;
        this.adminBuilder = adminBuilder;
        this.adminValidator = adminValidator;
    }

    public ResponseEntity<Admin> getAdminById(Long adminId){
        Optional<Admin> admin = adminRepository.findById(adminId);

        if (admin.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return ResponseEntity.status(HttpStatus.OK).body(admin.get());
    }

    public ResponseEntity<Admin> createAdmin(AdminRequestModel adminRequestModel) {

        adminValidator.validate(adminRequestModel);
        Admin admin = adminBuilder.build(new Admin(), adminRequestModel);

        if (adminRepository.findByEmail(adminRequestModel.getEmail()).isPresent())
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        Admin response = adminRepository.saveAndFlush(admin);
        admin.setId(response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }
}