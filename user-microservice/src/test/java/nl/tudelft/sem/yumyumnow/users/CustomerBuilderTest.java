package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.builders.CustomerFactory;
import nl.tudelft.sem.yumyumnow.users.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerBuilderTest {
    CustomerFactory customerBuilder;
    CustomerRequestModel customerRequestModel;
    Customer customer;
    @BeforeEach
    public void setUp(){
        customerBuilder = new CustomerFactory();
        Address address = new Address();
        customerRequestModel = new CustomerRequestModel(address,List.of("allergen1"), "email", "name", "surname");
        customer = new Customer();
    }

    @Test
    public void testEmptyInitialCustomer(){
        Customer resultCustomer = customerBuilder.build(customer, customerRequestModel);
        assertEquals(customerRequestModel.getName(), resultCustomer.getName());
        assertEquals(customerRequestModel.getSurname(), resultCustomer.getSurname());
        assertEquals(customerRequestModel.getEmail(), resultCustomer.getEmail());
        assertEquals(resultCustomer.getRole(), User.RoleEnum.CUSTOMER);
        assertEquals(resultCustomer.getBlocked(), false);
    }

    @Test
    public void testFilledInitialCustomer(){
        customer.setEmail("new email");
        customer.setBlocked(true);

        Customer resultCustomer = customerBuilder.build(customer, customerRequestModel);
        assertEquals(customerRequestModel.getName(), resultCustomer.getName());
        assertEquals(customerRequestModel.getSurname(), resultCustomer.getSurname());
        assertEquals(customerRequestModel.getEmail(), resultCustomer.getEmail());
        assertEquals(resultCustomer.getRole(), customer.getRole());
        assertEquals(resultCustomer.getBlocked(), customer.getBlocked());
    }
}
