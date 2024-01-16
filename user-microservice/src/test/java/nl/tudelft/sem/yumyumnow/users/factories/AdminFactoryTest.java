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
    transient Admin admin;
    @BeforeEach
    public void setUp(){
        adminFactory = new AdminFactory();
        adminRequestModel = new AdminRequestModel("name", "surname", "email");
        admin = new Admin();
    }

    @Test
    public void testEmptyInitialAdmin(){
        Admin resultAdmin = adminFactory.build(admin, adminRequestModel);
        assertEquals(adminRequestModel.getName(), resultAdmin.getName());
        assertEquals(adminRequestModel.getSurname(), resultAdmin.getSurname());
        assertEquals(adminRequestModel.getEmail(), resultAdmin.getEmail());
        assertEquals(resultAdmin.getRole(), User.RoleEnum.ADMIN);
        assertEquals(resultAdmin.getBlocked(), false);
    }

    @Test
    public void testFilledInitialAdmin(){
        admin.setEmail("new email");
        admin.setBlocked(true);

        Admin resultAdmin = adminFactory.build(admin, adminRequestModel);
        assertEquals(adminRequestModel.getName(), resultAdmin.getName());
        assertEquals(adminRequestModel.getSurname(), resultAdmin.getSurname());
        assertEquals(adminRequestModel.getEmail(), resultAdmin.getEmail());
        assertEquals(resultAdmin.getRole(), admin.getRole());
        assertEquals(resultAdmin.getBlocked(), admin.getBlocked());
    }
}
