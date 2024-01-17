package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.stereotype.Component;

@Component
public class AdminFactory implements UserFactory<Admin, AdminRequestModel> {
    /**
     * Builds an admin object (sets needed fields).
     *
     * @param adminRequestModel  the attributes that are being updated/posted
     *
     * @return return the updated Admin object
     */
    public Admin build(AdminRequestModel adminRequestModel) {

        Admin admin = new Admin();
        admin.setName(adminRequestModel.getName());
        admin.setSurname(adminRequestModel.getSurname());
        admin.setEmail(adminRequestModel.getEmail());
        admin.setRole(User.RoleEnum.ADMIN);
        admin.setBlocked(false);

        return admin;
    }
}
