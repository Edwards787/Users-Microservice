package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import nl.tudelft.sem.yumyumnow.users.services.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CourierController {
    private final CourierService courierService;
    @Autowired
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }
}
