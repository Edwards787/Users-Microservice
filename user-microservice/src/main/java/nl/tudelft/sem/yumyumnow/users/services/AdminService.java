package nl.tudelft.sem.yumyumnow.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.yumyumnow.users.builders.AdminBuilder;
import nl.tudelft.sem.yumyumnow.users.builders.Builder;
import nl.tudelft.sem.yumyumnow.users.builders.Director;
import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InitialAdminException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.validators.AdminValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminValidator adminValidator;
    Builder builder;
    Director director;


    /**
     * Constructor.
     *
     * @param adminRepository admin repository
     * @param adminValidator  admin validator
     * @param builder builder for admins
     * @param director director for admin creation
     *
     */
    @Autowired
    public AdminService(AdminRepository adminRepository,
                        AdminValidator adminValidator, Builder builder, Director director) {
        this.adminRepository = adminRepository;
        this.adminValidator = adminValidator;
        this.builder = builder;
        this.director = director;
    }

    public Admin getAdminById(Long adminId) throws UserNotFoundException {
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isEmpty()) {
            throw new UserNotFoundException();
        }
        else {
            return admin.get();
        }
    }
    /**
     * Service that handles the creation of an admin.
     *
     * @param adminRequestModel         request model of admin with provided attributes
     *
     * @return created admin object
     * @throws InvalidFieldException    throw exception in case invalid fields were provided
     * @throws MissingBodyException      throw exception in case a body was missing
     * @throws EmailAlreadyInUseException throw exception in case email was not provided
     */

    public Admin createAdmin(@NotNull AdminRequestModel adminRequestModel)
        throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException {

        adminValidator.validate(adminRequestModel);
        Admin dummy = new Admin();
        director.constructAdmin(builder, adminRequestModel);
        Admin admin = (Admin)builder.build(dummy);

        if (adminRepository.findByEmail(adminRequestModel.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException(adminRequestModel.getEmail());
        }

        Admin response = adminRepository.saveAndFlush(admin);
        admin.setId(response.getId());

        return admin;

    }

    public void deleteAdminById(Long adminId1, Long adminId2)
        throws InitialAdminException, NotAuthorizedException, UserNotFoundException {
        if (adminId1 == 0)
            throw new InitialAdminException();
        try{
            Admin admin = getAdminById(adminId2);
        } catch (UserNotFoundException exception){
            throw new NotAuthorizedException();
        }
        try {
            adminRepository.deleteById(adminId1);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
        }
    }

    public void updateAdminById(Long adminId1, Long adminId2, @NotNull AdminRequestModel adminRequestModel)
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        Admin adminInitial = getAdminById(adminId1);
        try {
            getAdminById(adminId2);
        } catch (UserNotFoundException exception){
            throw new NotAuthorizedException();
        }

        adminValidator.validate(adminRequestModel);
        director.constructAdmin(builder, adminRequestModel);
        Admin admin = (Admin) builder.build(adminInitial);
        adminRepository.saveAndFlush(admin);
    }

    public ResponseEntity<List<Long>> getRefundRequests(Long userId) {
        return ResponseEntity.ok(new ArrayList<>());

    }
}