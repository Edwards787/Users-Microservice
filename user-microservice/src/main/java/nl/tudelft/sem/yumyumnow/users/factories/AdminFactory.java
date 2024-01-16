package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.stereotype.Service;

@Service
public class AdminFactory extends UserFactory {
    /**
     * Builds an admin object (sets needed fields).
     *
     * @param adminRequestModel  the attributes that are being updated/posted
     *
     * @return return the updated Admin object
     */
    public Admin build(AdminRequestModel adminRequestModel) {

        Admin admin = (Admin) super.build(adminRequestModel);
        admin.setRole(User.RoleEnum.ADMIN);

        return admin;
    }
}
