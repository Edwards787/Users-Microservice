package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.controllers.AdminController;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminControllerTest {
    AdminService adminService;
    AdminController adminController;
    Admin admin1;
    Admin admin2;

    @BeforeEach
    public void setUp(){
        adminService = mock(AdminService.class);
        adminController = new AdminController(adminService);
    }

    @BeforeEach
    public void setUpEntities(){
        admin1 = new Admin();
        admin1.setSurname("surname 1");
        admin1.setName("name 1");
        admin1.setId(0L);

        admin2 = new Admin();
        admin2.setSurname("surname 2");
        admin2.setName("name 2");
        admin2.setId(1L);

    }

    @Test
    public void testAdminGetByIdValid(){
        when(adminService.getAdminById(0L)).thenReturn(admin1);
        assertThat(adminController.getAdminById(0L)).isEqualTo(ResponseEntity.ok(admin1));
    }
}
