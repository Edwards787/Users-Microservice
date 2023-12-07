package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    AdminRepository adminRepository;
    @Autowired
    public AdminService(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public Admin createAdmin(AdminRequestModel adminRequestModel){
        Admin admin = new Admin();
        admin.setBlocked(false);
        admin.setName(adminRequestModel.getName());
        admin.setSurname(adminRequestModel.getSurname());
        admin.setEmail(adminRequestModel.getEmail());
        admin.setRole(User.RoleEnum.ADMIN);

        Admin response = adminRepository.saveAndFlush(admin);
        admin.setId(response.getId());

        return admin;
    }


}
