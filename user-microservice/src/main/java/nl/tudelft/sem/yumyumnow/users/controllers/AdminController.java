package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.AdminApi;
import nl.tudelft.sem.yumyumnow.users.model.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AdminController implements AdminApi {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
}
