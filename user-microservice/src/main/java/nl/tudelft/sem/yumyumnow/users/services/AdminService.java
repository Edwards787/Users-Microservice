package nl.tudelft.sem.yumyumnow.users.services;

import java.util.Optional;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public ResponseEntity<Admin> getAdminById(Long adminId) throws UserNotFoundException {
        Optional<Admin> admin = adminRepository.findById(adminId);

        if (admin.isEmpty()) throw new UserNotFoundException();

        return ResponseEntity.status(HttpStatus.OK).body(admin.get());
    }

    public ResponseEntity<Admin> createAdmin(@NotNull AdminRequestModel adminRequestModel) throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException {
        if (adminRequestModel == null)
            throw new MissingBodyException();

        if (adminRequestModel.getSurname() == null || adminRequestModel.getSurname().equals(""))
            throw new InvalidFieldException("surname", adminRequestModel.getSurname());
        Admin admin = new Admin();
        admin.setBlocked(false);
        admin.setName(adminRequestModel.getName());
        admin.setSurname(adminRequestModel.getSurname());
        admin.setEmail(adminRequestModel.getEmail());
        admin.setRole(User.RoleEnum.ADMIN);

        if (adminRepository.findByEmail(adminRequestModel.getEmail()).isPresent())
            throw new EmailAlreadyInUseException(adminRequestModel.getEmail());

        Admin response = adminRepository.saveAndFlush(admin);
        admin.setId(response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }
}