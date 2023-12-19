package nl.tudelft.sem.yumyumnow.users.services;

import java.util.Optional;
import javax.validation.constraints.NotNull;

import nl.tudelft.sem.yumyumnow.users.builders.AdminBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.controllers.validators.AdminValidator;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public ResponseEntity<Admin> getAdminById(Long adminId) throws UserNotFoundException {
        Optional<Admin> admin = adminRepository.findById(adminId);

        if (admin.isEmpty()) throw new UserNotFoundException();

        return ResponseEntity.status(HttpStatus.OK).body(admin.get());
    }


    public ResponseEntity<Admin> createAdmin(@NotNull AdminRequestModel adminRequestModel) throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException {
        adminValidator.validate(adminRequestModel);
        Admin admin = adminBuilder.build(new Admin(), adminRequestModel);

        Admin response = adminRepository.saveAndFlush(admin);
        admin.setId(response.getId());

        if (adminRepository.findByEmail(adminRequestModel.getEmail()).isPresent())
            throw new EmailAlreadyInUseException(adminRequestModel.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }
}