package nl.tudelft.sem.yumyumnow.users.services;

import javax.validation.constraints.NotNull;
import nl.tudelft.sem.yumyumnow.users.builders.AdminBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.validators.AdminValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminValidator adminValidator;
    private final AdminBuilder adminBuilder;

    /**
     * Constructor.
     *
     * @param adminRepository admin repository
     * @param adminBuilder    admin builder
     * @param adminValidator  admin validator
     *
     */
    @Autowired
    public AdminService(AdminRepository adminRepository, AdminBuilder adminBuilder,
                        AdminValidator adminValidator) {
        this.adminRepository = adminRepository;
        this.adminBuilder = adminBuilder;
        this.adminValidator = adminValidator;
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
        Admin admin = adminBuilder.build(dummy, adminRequestModel);

        if (adminRepository.findByEmail(adminRequestModel.getEmail()).isPresent()) {
            throw new EmailAlreadyInUseException(adminRequestModel.getEmail());
        }

        Admin response = adminRepository.saveAndFlush(admin);
        admin.setId(response.getId());

        return admin;
    }
}