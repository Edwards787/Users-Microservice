package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourierFactoryTest {
    transient CourierFactory courierFactory;
    transient CourierRequestModel courierRequestModel;
    @BeforeEach
    public void setUp(){
        courierFactory = new CourierFactory();
        courierRequestModel = new CourierRequestModel("name", "surname", "email");
    }

    @Test
    public void testEmptyInitialAdmin(){
        Courier resultCourier = courierFactory.build(courierRequestModel);
        assertEquals(courierRequestModel.getName(), resultCourier.getName());
        assertEquals(courierRequestModel.getSurname(), resultCourier.getSurname());
        assertEquals(courierRequestModel.getEmail(), resultCourier.getEmail());
        assertEquals(resultCourier.getRole(), User.RoleEnum.COURIER);
        assertEquals(resultCourier.getBlocked(), false);
    }
}