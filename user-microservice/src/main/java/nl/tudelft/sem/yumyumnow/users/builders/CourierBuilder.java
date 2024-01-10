package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.*;
import org.springframework.stereotype.Service;

@Service
public class CourierBuilder extends UserBuilder{
    public Courier build(Courier courier, CourierRequestModel courierRequestModel) {
        super.build(courier, courierRequestModel);
        courier.setRole(User.RoleEnum.COURIER);
        return courier;
    }

}
