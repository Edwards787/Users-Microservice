package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminFactoryTest {
    transient AdminFactory adminFactory;
    transient AdminRequestModel adminRequestModel;
    @BeforeEach
    public void setUp(){
        adminFactory = new AdminFactory();
        adminRequestModel = new AdminRequestModel("name", "surname", "email");
    }

    @Test
    public void testEmptyInitialAdmin(){
        Admin resultAdmin = adminFactory.build(adminRequestModel);
        assertEquals(adminRequestModel.getName(), resultAdmin.getName());
        assertEquals(adminRequestModel.getSurname(), resultAdmin.getSurname());
        assertEquals(adminRequestModel.getEmail(), resultAdmin.getEmail());
        assertEquals(resultAdmin.getRole(), User.RoleEnum.ADMIN);
        assertEquals(resultAdmin.getBlocked(), false);
    }
}
