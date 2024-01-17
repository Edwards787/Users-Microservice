package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.stereotype.Component;

@Component
public class CourierFactory implements UserFactory<Courier, CourierRequestModel> {
    /**
     * Builds a courier object.
     *
     * @param courierRequestModel courier request model
     * @return courier object
     */
    @SuppressWarnings("unused")
    public Courier build(CourierRequestModel courierRequestModel) {

        Courier courier = new Courier();
        courier.setName(courierRequestModel.getName());
        courier.setSurname(courierRequestModel.getSurname());
        courier.setEmail(courierRequestModel.getEmail());
        courier.setRole(User.RoleEnum.COURIER);
        courier.setBlocked(false);

        return courier;
    }
}
