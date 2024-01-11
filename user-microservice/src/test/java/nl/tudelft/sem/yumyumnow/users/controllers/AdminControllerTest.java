package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.controllers.AdminController;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AdminControllerTest {
    private AdminService adminService;
    private AdminController adminController;
    private Admin admin1;
    private Admin admin2;
    private AdminRequestModel adminRequestModel;

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
        admin1.setEmail("email 1");
        admin1.setRole(User.RoleEnum.ADMIN);
        admin1.setId(0L);

        admin2 = new Admin();
        admin2.setSurname("surname 2");
        admin2.setEmail("email 2");
        admin2.setRole(User.RoleEnum.ADMIN);
        admin2.setName("name 2");
        admin2.setId(1L);

        adminRequestModel = new AdminRequestModel("name 3", "surname 3", "email 3");
    }

    @Test
    public void testAdminCreateValid()
        throws EmailAlreadyInUseException, MissingBodyException, InvalidFieldException,
        UserNotFoundException, NotAuthorizedException {
        when(adminService.createAdmin(adminRequestModel)).thenReturn(admin1);
        assertThat(adminController.createAdmin(adminRequestModel)).isEqualTo(ResponseEntity.ok(admin1));
    }

    @Test
    public void testAdminCreateNullRequest()
        throws EmailAlreadyInUseException, MissingBodyException, InvalidFieldException,
        UserNotFoundException, NotAuthorizedException {
        when(adminService.createAdmin(null)).thenThrow(new MissingBodyException());
        assertThrows(ResponseStatusException.class, () -> {
            adminController.createAdmin(null);
        });
        try{
            adminController.createAdmin(null);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getReason()).isEqualTo("Body is missing");
        }
    }

    @Test
    public void testAdminCreateNoEmail()
        throws EmailAlreadyInUseException, MissingBodyException, InvalidFieldException,
        UserNotFoundException, NotAuthorizedException {
        adminRequestModel.setEmail("");
        when(adminService.createAdmin(adminRequestModel)).thenThrow(new InvalidFieldException("email", ""));
        assertThrows(ResponseStatusException.class, () -> {
            adminController.createAdmin(adminRequestModel);
        });
        try{
            adminController.createAdmin(adminRequestModel);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(e.getReason()).isEqualTo("Field 'email' with value '' is invalid");
        }
    }

    @Test
    public void testAdminCreateEmailExists()
        throws EmailAlreadyInUseException, MissingBodyException, InvalidFieldException,
        UserNotFoundException, NotAuthorizedException {
        when(adminService.createAdmin(adminRequestModel)).thenThrow(new EmailAlreadyInUseException("email"));
        assertThrows(ResponseStatusException.class, () -> {
            adminController.createAdmin(adminRequestModel);
        });
        try{
            adminController.createAdmin(adminRequestModel);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(e.getReason()).isEqualTo("User with email 'email' already exists");
        }
    }

    @Test
    public void testAdminGetByIdValid() throws UserNotFoundException {
        when(adminService.getAdminById(0L)).thenReturn(admin1);
        assertThat(adminController.getAdminById(0L)).isEqualTo(ResponseEntity.ok(admin1));
    }
    @Test
    public void testAdminGetByIdInvalid() throws UserNotFoundException {
        when(adminService.getAdminById(0L)).thenThrow(new UserNotFoundException());
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
    public void testDeleteAdminValid()
        throws UserNotFoundException, InitialAdminException, NotAuthorizedException,
        MissingBodyException, InvalidFieldException {
        doNothing().when(adminService).deleteAdminById(1L, 2L);
        assertThat(adminController.deleteAdmin(1L, 2L)).isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        verify(adminService).deleteAdminById(1L, 2L);
    }

    @Test
    public void testDeleteInitialAdmin()
        throws UserNotFoundException, InitialAdminException, NotAuthorizedException,
        MissingBodyException, InvalidFieldException {
        doThrow(new InitialAdminException()).when(adminService).deleteAdminById(0L, 1L);

        assertThrows(ResponseStatusException.class, () -> {
            adminController.deleteAdmin(0L, 1L);
        });
        try{
            adminController.deleteAdmin(0L, 1L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Initial admin can not be deleted");
        }
    }

    @Test
    public void testDeleteAdminUnauthorized()
        throws UserNotFoundException, InitialAdminException, NotAuthorizedException,
        MissingBodyException, InvalidFieldException {
        doThrow(new NotAuthorizedException()).when(adminService).deleteAdminById(2L, 0L);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.deleteAdmin(2L, 0L);
        });
        try{
            adminController.deleteAdmin(2L, 0L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus().value()).isEqualTo(403);
            assertThat(e.getReason()).isEqualTo("Unauthorized action");
        }
    }

    @Test
    public void testDeleteAdminNotFound()
        throws UserNotFoundException, InitialAdminException, NotAuthorizedException,
        MissingBodyException, InvalidFieldException {
        doThrow(new UserNotFoundException()).when(adminService).deleteAdminById(3L, 0L);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.deleteAdmin(3L, 0L);
        });
        try{
            adminController.deleteAdmin(3L, 0L);
        }catch (ResponseStatusException e){
            assertThat(e.getStatus().value()).isEqualTo(404);
            assertThat(e.getReason()).isEqualTo("Admin not found");
        }
    }
    @Test
    public void testUpdateAdminValid()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        doNothing().when(adminService).updateAdminById(1L, 1L, adminRequestModel);

        assertThat(adminController.updateAdmin(1L, 1L, adminRequestModel))
            .isEqualTo(new ResponseEntity<>(HttpStatus.OK));
        verify(adminService).updateAdminById(1L, 1L, adminRequestModel);
    }
    @Test
    public void testUpdateAdminNullRequest()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {
        doThrow(new MissingBodyException()).when(adminService)
            .updateAdminById(3L, 0L, null);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, null);
        });
        try {
            adminController.updateAdmin(3L, 0L, null);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Body not present");
        }
    }
    @Test
    public void testUpdateAdminNoEmail()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        adminRequestModel.setEmail("");

        doThrow(new InvalidFieldException("email", "")).when(
            adminService).updateAdminById(3L, 0L, adminRequestModel);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        });
        try {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Field 'email' with value '' is invalid");
        }
    }
    @Test
    public void testUpdateAdminNullEmail()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        adminRequestModel.setEmail(null);

        doThrow(new InvalidFieldException("email", "null")).when(
            adminService).updateAdminById(3L, 0L, adminRequestModel);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        });
        try {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Field 'email' with value 'null' is invalid");
        }
    }
    @Test
    public void testUpdateAdminNoName()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        adminRequestModel.setName("");

        doThrow(new InvalidFieldException("name", "")).when(
            adminService).updateAdminById(3L, 0L, adminRequestModel);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        });
        try {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Field 'name' with value '' is invalid");
        }
    }
    @Test
    public void testUpdateAdminNullName()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        adminRequestModel.setName(null);

        doThrow(new InvalidFieldException("name", "null")).when(
            adminService).updateAdminById(3L, 0L, adminRequestModel);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        });
        try {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Field 'name' with value 'null' is invalid");
        }
    }

    @Test
    public void testUpdateAdminNoSurname()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        adminRequestModel.setSurname("");

        doThrow(new InvalidFieldException("surname", "")).when(
            adminService).updateAdminById(3L, 0L, adminRequestModel);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        });
        try {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Field 'surname' with value '' is invalid");
        }
    }
    @Test
    public void testUpdateAdminNullSurname()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        adminRequestModel.setSurname(null);

        doThrow(new InvalidFieldException("surname", "null")).when(
            adminService).updateAdminById(3L, 0L, adminRequestModel);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        });
        try {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(400);
            assertThat(e.getReason()).isEqualTo("Field 'surname' with value 'null' is invalid");
        }
    }
    @Test
    public void testUpdateAdminNotFound()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        doThrow(new UserNotFoundException()).when(
            adminService).updateAdminById(3L, 0L, adminRequestModel);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        });
        try {
            adminController.updateAdmin(3L, 0L, adminRequestModel);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(404);
            assertThat(e.getReason()).isEqualTo("Admin not found");
        }
    }
    @Test
    public void testUpdateAdminUnauthorized()
        throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
        InvalidFieldException {

        doThrow(new NotAuthorizedException()).when(
            adminService).updateAdminById(3L, 1L, adminRequestModel);
        assertThrows(ResponseStatusException.class, () -> {
            adminController.updateAdmin(3L, 1L, adminRequestModel);
        });
        try {
            adminController.updateAdmin(3L, 1L, adminRequestModel);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatus().value()).isEqualTo(403);
            assertThat(e.getReason()).isEqualTo("Unauthorized action");
        }
    }
}
