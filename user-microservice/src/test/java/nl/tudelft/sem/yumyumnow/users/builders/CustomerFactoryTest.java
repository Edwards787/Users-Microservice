package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CustomerFactoryTest {
    transient CustomerFactory customerFactory;
    transient CustomerRequestModel customerRequestModel;
    transient Customer customer;
    @BeforeEach
    public void setUp(){
        customerFactory = new CustomerFactory();
        customerRequestModel = new CustomerRequestModel(new Address(), new ArrayList<>(), "surname", "email", "name");
        customer = new Customer();
    }

    @Test
    public void testEmptyInitialAdmin(){
        Customer resultCustomer = customerFactory.build(customer, customerRequestModel);
        assertEquals(customerRequestModel.getName(), resultCustomer.getName());
        assertEquals(customerRequestModel.getSurname(), resultCustomer.getSurname());
        assertEquals(customerRequestModel.getEmail(), resultCustomer.getEmail());
        assertEquals(resultCustomer.getRole(), User.RoleEnum.CUSTOMER);
        assertEquals(resultCustomer.getBlocked(), false);
    }

    @Test
    public void testFilledInitialAdmin(){
        customer.setEmail("new email");
        customer.setBlocked(true);

        Customer resultCustomer = customerFactory.build(customer, customerRequestModel);
        assertEquals(customerRequestModel.getName(), resultCustomer.getName());
        assertEquals(customerRequestModel.getSurname(), resultCustomer.getSurname());
        assertEquals(customerRequestModel.getEmail(), resultCustomer.getEmail());
        assertEquals(resultCustomer.getRole(), customer.getRole());
        assertEquals(resultCustomer.getBlocked(), customer.getBlocked());
    }


}