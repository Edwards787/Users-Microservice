package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.stereotype.Service;

@Service
public class CourierBuilder extends UserBuilder {
    /**
     * Builds a courier object.
     *
     * @param courier              courier object
     * @param courierRequestModel courier request model
     * @return courier object
     */
    @SuppressWarnings("unused")
    public Courier build(Courier courier, CourierRequestModel courierRequestModel) {
        super.build(courier, courierRequestModel);
        courier.setRole(User.RoleEnum.COURIER);
        return courier;
    }
}
