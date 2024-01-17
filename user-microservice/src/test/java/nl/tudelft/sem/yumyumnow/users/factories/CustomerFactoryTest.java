package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CustomerFactoryTest {
    transient CustomerFactory customerFactory;
    transient CustomerRequestModel customerRequestModel;
    @BeforeEach
    public void setUp(){
        customerFactory = new CustomerFactory();
        customerRequestModel = new CustomerRequestModel(new Address(), new ArrayList<>(), "surname", "email", "name");
    }

    @Test
    public void testEmptyInitialAdmin(){
        Customer resultCustomer = customerFactory.build(customerRequestModel);
        assertEquals(customerRequestModel.getName(), resultCustomer.getName());
        assertEquals(customerRequestModel.getSurname(), resultCustomer.getSurname());
        assertEquals(customerRequestModel.getEmail(), resultCustomer.getEmail());
        assertEquals(resultCustomer.getRole(), User.RoleEnum.CUSTOMER);
        assertEquals(resultCustomer.getBlocked(), false);
    }
}