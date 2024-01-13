package nl.tudelft.sem.yumyumnow.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import nl.tudelft.sem.yumyumnow.users.builders.AdminBuilder;
import nl.tudelft.sem.yumyumnow.users.builders.Builder;
import nl.tudelft.sem.yumyumnow.users.builders.Director;
import nl.tudelft.sem.yumyumnow.users.builders.Ubuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InitialAdminException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.NotAuthorizedException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.Accessibility;
import nl.tudelft.sem.yumyumnow.users.validation.objects.AuthorizationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidationObject;
import nl.tudelft.sem.yumyumnow.users.validation.objects.Validator;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidatorConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final transient AdminRepository adminRepository;
    private final transient UserRepository userRepository;
    private final transient ValidatorConstructor validatorConstructor;
    private final transient AdminBuilder adminBuilder;
    private final AdminRepository adminRepository;
    Builder builder;
    Director director;

    private final UserRepository userRepository;
    private final ValidatorConstructor validatorConstructor;

    @Autowired
    public AdminService(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.validatorConstructor = new ValidatorConstructor();
        this.builder = new Ubuilder();
        this.director = new Director();
        this.userRepository = userRepository;
    }

    /**
     * Service that handles the retrieval of an admin by id.
     *
     * @return admin object
     */
    public Admin getAdminById(Long adminId) throws UserNotFoundException {
        Optional<Admin> admin = adminRepository.findById(adminId);
        if (admin.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            return admin.get();
        }
    }

    /**
     * Service that handles the creation of an admin.
     *
     * @param adminRequestModel request model of admin with provided attributes
     * @return created admin object
     * @throws InvalidFieldException      throw exception in case invalid fields were provided
     * @throws MissingBodyException       throw exception in case a body was missing
     * @throws EmailAlreadyInUseException throw exception in case email was not provided
     */

    public Admin createAdmin(@NotNull AdminRequestModel adminRequestModel)
            throws InvalidFieldException, MissingBodyException, EmailAlreadyInUseException,
            UserNotFoundException, NotAuthorizedException {

        Validator validator = validatorConstructor.userWithoutAuthorization();
        validator.handle(new ValidationObject(adminRequestModel, null));

        Admin admin = new Admin();
        adminBuilder.build(admin, adminRequestModel);

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

    /**
     * Service that handles the deletion of an admin by id.
     *
     * @param adminId1 id of admin to be deleted
     * @param adminId2 id of admin that is requesting the deletion
     * @throws InitialAdminException  throw exception in case initial admin is being deleted
     * @throws NotAuthorizedException throw exception in case the user with adminId2 is not an admin
     * @throws UserNotFoundException  throw exception in case the user with adminId1 was not found
     */
    public void deleteAdminById(Long adminId1, Long adminId2)
            throws InitialAdminException, NotAuthorizedException, UserNotFoundException,
            MissingBodyException, InvalidFieldException {
        if (adminId1 == 0) {
            throw new InitialAdminException();
        }

        Validator validator = validatorConstructor.userAuthorization();
        validator.handle(
                new ValidationObject(
                        null,
                        new AuthorizationObject(
                                adminId1, adminId2, userRepository, Accessibility.USER_AND_ADMIN
                        )));

        try {
            adminRepository.deleteById(adminId1);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
        }
    }

    /**
     * Service that handles the update of an admin by id.
     *
     * @param adminId1          id of admin to be updated
     * @param adminId2          id of admin that is requesting the update
     * @param adminRequestModel request model of admin with provided attributes
     * @throws UserNotFoundException  throw exception in case the user with adminId1 was not found
     * @throws NotAuthorizedException throw exception in case the user with adminId2 is not an admin
     * @throws MissingBodyException   throw exception in case a body was missing
     * @throws InvalidFieldException  throw exception in case invalid fields were provided
     */
    public void updateAdminById(Long adminId1, Long adminId2, @NotNull AdminRequestModel adminRequestModel)
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        Admin adminInitial = getAdminById(adminId1);

        Validator validator = validatorConstructor.userWithAuthorization();
        validator.handle(new ValidationObject(
                adminRequestModel,
                new AuthorizationObject(
                        adminId1, adminId2, userRepository, Accessibility.USER_AND_ADMIN
                )));
        adminBuilder.build(admin, adminRequestModel);
        validator.handle(new ValidationObject(adminRequestModel, new AuthorizationObject
            (adminId1, adminId2, userRepository, Accessibility.USER_AND_ADMIN)));

        director.constructAdmin(builder, adminRequestModel);
        Admin admin = (Admin) builder.build(adminInitial);
        adminRepository.saveAndFlush(admin);
    }

    @SuppressWarnings("unused")
    public ResponseEntity<List<Long>> getRefundRequests(Long userId) {
        return ResponseEntity.ok(new ArrayList<>());

    }
}