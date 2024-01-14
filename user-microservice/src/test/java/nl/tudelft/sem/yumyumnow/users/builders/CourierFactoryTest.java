package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourierFactoryTest {
    transient CourierFactory courierFactory;
    transient CourierRequestModel courierRequestModel;
    transient Courier courier;
    @BeforeEach
    public void setUp(){
        courierFactory = new CourierFactory();
        courierRequestModel = new CourierRequestModel("name", "surname", "email");
        courier = new Courier();
    }

    @Test
    public void testEmptyInitialAdmin(){
        Courier resultCourier = courierFactory.build(courier, courierRequestModel);
        assertEquals(courierRequestModel.getName(), resultCourier.getName());
        assertEquals(courierRequestModel.getSurname(), resultCourier.getSurname());
        assertEquals(courierRequestModel.getEmail(), resultCourier.getEmail());
        assertEquals(resultCourier.getRole(), User.RoleEnum.COURIER);
        assertEquals(resultCourier.getBlocked(), false);
    }

    @Test
    public void testFilledInitialAdmin(){
        courier.setEmail("new email");
        courier.setBlocked(true);

        Courier resultCourier = courierFactory.build(courier, courierRequestModel);
        assertEquals(courierRequestModel.getName(), resultCourier.getName());
        assertEquals(courierRequestModel.getSurname(), resultCourier.getSurname());
        assertEquals(courierRequestModel.getEmail(), resultCourier.getEmail());
        assertEquals(resultCourier.getRole(), courier.getRole());
        assertEquals(resultCourier.getBlocked(), courier.getBlocked());
    }

}