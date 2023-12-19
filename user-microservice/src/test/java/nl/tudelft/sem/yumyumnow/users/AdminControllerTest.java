package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.controllers.AdminController;
import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class AdminControllerTest {
    AdminService adminService;
    AdminController adminController;
    Admin admin1;
    Admin admin2;
    AdminRequestModel adminRequestModel;

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

        adminRequestModel = new AdminRequestModel("name 1", "surname 3", "email 3");
    }

    @Test
    public void testAdminGetByIdValid(){
        when(adminService.getAdminById(0L)).thenReturn(admin1);
        assertThat(adminController.getAdminById(0L)).isEqualTo(ResponseEntity.ok(admin1));
    }
    @Test
    public void testAdminGetByIdInvalid(){
        when(adminService.getAdminById(0L)).thenThrow(new StatusException(HttpStatus.NOT_FOUND, "Admin not found"));
        assertThrows(ResponseStatusException.class, () -> {
            adminController.getAdminById(0L);
            });
        try{
            adminController.getAdminById(0L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(e.getReason()).isEqualTo("Admin not found");
        }
    }

    @Test
    public void testDeleteAdminValid(){
        doNothing().when(adminService).deleteAdminById(1L, 2L);
        assertThat(adminController.deleteAdmin(1L, 2L)).isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        verify(adminService).deleteAdminById(1L, 2L);
    }

    @Test
    public void testDeleteInitialAdmin(){
        doThrow(new StatusException(HttpStatus.valueOf(403), "Initial admin cannot be deleted")).when(adminService).deleteAdminById(0L, 1L);

        assertThrows(ResponseStatusException.class, () -> {
            adminController.deleteAdmin(0L, 1L);
        });
        try{
            adminController.deleteAdmin(0L, 1L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus().value()).isEqualTo(403);
            assertThat(e.getReason()).isEqualTo("Initial admin cannot be deleted");
        }
    }

    @Test
    public void testDeleteAdminUnauthorized(){
        doThrow(new StatusException(HttpStatus.valueOf(403), "Unauthorized")).when(adminService).deleteAdminById(2L, 0L);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.deleteAdmin(2L, 0L);
        });
        try{
            adminController.deleteAdmin(2L, 0L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus().value()).isEqualTo(403);
            assertThat(e.getReason()).isEqualTo("Unauthorized");
        }
    }

    @Test
    public void testDeleteAdminNotFound(){
        doThrow(new StatusException(HttpStatus.valueOf(404), "User not found")).when(adminService).deleteAdminById(3L, 0L);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.deleteAdmin(3L, 0L);
        });
        try{
            adminController.deleteAdmin(3L, 0L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus().value()).isEqualTo(404);
            assertThat(e.getReason()).isEqualTo("User not found");
        }
    }

    @Test
    public void testUpdateAdminValid(){
        doNothing().when(adminService).updateAdminById(1L, 1L, adminRequestModel);

        assertThat(adminController.updateAdmin(1L, 1L, adminRequestModel))
            .isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        verify(adminService).updateAdminById(1L, 1L, adminRequestModel);
    }
    @Test
    public void testUpdateAdminNullRequest() {
        doThrow(new StatusException(HttpStatus.BAD_REQUEST, "No admin update model present")).when(
            adminService).updateAdminById(3L, 0L, null);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, null);
        });
        try {
            adminController.updateAdmin(3L, 0L, null);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("No admin update model present");
        }
    }
    @Test
    public void testUpdateAdminNoEmail() {
        doThrow(new StatusException(HttpStatus.BAD_REQUEST, "No admin update model present")).when(
            adminService).updateAdminById(3L, 0L, null);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, null);
        });
        try {
            adminController.updateAdmin(3L, 0L, null);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("No admin update model present");
        }
    }

}
