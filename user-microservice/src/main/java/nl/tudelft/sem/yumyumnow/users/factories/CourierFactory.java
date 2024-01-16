package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.stereotype.Service;

@Service
public class CourierFactory extends UserFactory {
    /**
     * Builds a courier object.
     *
     * @param courierRequestModel courier request model
     * @return courier object
     */
    @SuppressWarnings("unused")
    public Courier build(CourierRequestModel courierRequestModel) {

        Courier courier = (Courier) super.build(courierRequestModel);
        courier.setRole(User.RoleEnum.COURIER);

        return courier;
    }
}
