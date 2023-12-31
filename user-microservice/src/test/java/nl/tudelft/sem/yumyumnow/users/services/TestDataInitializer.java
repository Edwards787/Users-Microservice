package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;

public class TestDataInitializer {
    public static CourierRequestModel createValidCourierRequestModel() {
        return new CourierRequestModel("johndoe@gmail.com", "John", "Doe");
    }

    public static CourierRequestModel createInvalidCourierRequestModel() {
        return new CourierRequestModel("invalid_email", "John", "Doe");
    }

    public static Courier createDummyCourier() {
        return new Courier();
    }
}
