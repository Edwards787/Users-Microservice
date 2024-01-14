package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.AdminFactory;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidatorConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AdminServiceTest {
    transient AdminRepository adminRepository;
    transient UserRepository userRepository;
    transient ValidatorConstructor validatorConstructor;
    transient AdminFactory adminBuilder;
    transient AdminService adminService;
    transient Admin admin1;
    transient Admin admin2;
    transient AdminRequestModel adminRequestModel;

    @BeforeEach
    public void setUp(){
        adminRepository = mock(AdminRepository.class);
        userRepository = mock(UserRepository.class);
        validatorConstructor = new ValidatorConstructor();
        adminBuilder = new AdminFactory();
        adminService = new AdminService(adminRepository, userRepository, adminBuilder);
    }

    @BeforeEach
    public void setUpEntities(){
        admin1 = new Admin();
        admin1.setSurname("surname 1");
        admin1.setName("name 1");
        admin1.setEmail("email1@mail.com");
        admin1.setRole(User.RoleEnum.ADMIN);
        admin1.setId(0L);

        admin2 = new Admin();
        admin2.setSurname("surname 2");
        admin2.setName("name 2");
        admin2.setEmail("email2@mail.com");
        admin2.setRole(User.RoleEnum.ADMIN);
        admin2.setId(1L);

        adminRequestModel = new AdminRequestModel("email1@mail.com", "name 1", "surname 1");

        when(userRepository.findById(0L)).thenReturn(Optional.of(admin1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin2));
        when(adminRepository.findById(0L)).thenReturn(Optional.of(admin1));
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin2));
    }
    @Test
    public void testGetAdminByIdValid() throws UserNotFoundException {
        when(adminRepository.findById(0L)).thenReturn(Optional.of(admin1));
        assertThat(adminService.getAdminById(0L)).isEqualTo(admin1);
    }

    @Test
    public void testGetAdminByIdInvalid() throws UserNotFoundException {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            adminService.getAdminById(1L);
        });
    }

    @Test
    public void testCreateAdminValid()
            throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException,
            UserNotFoundException, NotAuthorizedException {
        when(adminRepository.findByEmail("email1@mail.com")).thenReturn(Optional.empty());
        when(adminRepository.saveAndFlush(adminBuilder.build(new Admin(), adminRequestModel))).thenReturn(admin1);
        assertThat(adminService.createAdmin(adminRequestModel)).isEqualTo(admin1);
    }
    @Test
    public void testCreateAdminInvalid() {
        when(adminRepository.findByEmail("email1@mail.com")).thenReturn(Optional.of(admin1));
        assertThrows(EmailAlreadyInUseException.class, () -> {
            adminService.createAdmin(adminRequestModel);
        });
    }
    @Test
    public void testDeleteAdminByIdValid()
            throws UserNotFoundException, InitialAdminException, NotAuthorizedException,
            MissingBodyException, InvalidFieldException {
        when(adminRepository.findById(0L)).thenReturn(Optional.of(admin1));
        doNothing().when(adminRepository).deleteById(1L);

        adminService.deleteAdminById(1L, 0L);

        verify(userRepository, times(2)).findById(0L);
        verify(adminRepository).deleteById(1L);
    }
    @Test
    public void testDeleteAdminByIdNotFound() {
        when(adminRepository.findById(0L)).thenReturn(Optional.of(admin1));
        doThrow(new EmptyResultDataAccessException(0)).when(adminRepository).deleteById(1L);
        assertThrows(UserNotFoundException.class, () -> {
            adminService.deleteAdminById(1L, 0L);
            verify(adminRepository).findById(0L);
        });
    }
    @Test
    public void testDeleteAdminByIdNotAuthorized(){
        User user3 = new User();
        user3.setRole(User.RoleEnum.VENDOR);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user3));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user3));
        assertThrows(NotAuthorizedException.class, () -> {
            adminService.deleteAdminById(2L, 1L);
            verify(adminRepository).findById(1L);
        });
    }
    @Test
    public void testDeleteAdminByIdInitialAdmin(){
        assertThrows(InitialAdminException.class, () -> {
            adminService.deleteAdminById(0L, 1L);
        });
    }

    @Test
    public void testUpdateAdminByIdValid()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        when(adminRepository.findById(0L)).thenReturn(Optional.of(admin1));
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin2));

        adminService.updateAdminById(1L, 0L, adminRequestModel);

        verify(adminRepository).saveAndFlush(admin1);
        assertThat(admin1).isEqualTo(admin2);
    }

    @Test
    public void testUpdateAdminByIdNotFound()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            adminService.updateAdminById(1L, 0L, adminRequestModel);
            verify(adminRepository).findById(1L);
        });
    }

    @Test
    public void testUpdateAdminByIdUnauthorized()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        User user = new User();
        user.setRole(User.RoleEnum.VENDOR);

        when(adminRepository.findById(0L)).thenReturn(Optional.of(admin1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(NotAuthorizedException.class, () -> {
            adminService.updateAdminById(0L, 2L, adminRequestModel);
            verify(adminRepository).findById(1L);
            verify(adminRepository).findById(0L);
        });
    }
    @Test
    public void testUpdateAdminByIdMissingBody()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        assertThrows(MissingBodyException.class, () -> {
            adminService.updateAdminById(0L, 1L, null);
            verify(adminRepository).findById(1L);
            verify(adminRepository).findById(0L);
        });
    }
    @Test
    public void testUpdateAdminByIdNoEmail()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        adminRequestModel.setEmail("");

        assertThrows(InvalidFieldException.class, () -> {
            adminService.updateAdminById(0L, 1L, adminRequestModel);
            verify(adminRepository).findById(1L);
            verify(adminRepository).findById(0L);
        });

        try{
            adminService.updateAdminById(0L, 1L, adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("email");
            assertThat(e.getMessage()).isEqualTo("Field 'email' with value '' is invalid");
        }
    }
    @Test
    public void testUpdateAdminByIdNullEmail()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        adminRequestModel.setEmail(null);

        assertThrows(InvalidFieldException.class, () -> {
            adminService.updateAdminById(0L, 1L, adminRequestModel);
            verify(adminRepository).findById(1L);
            verify(adminRepository).findById(0L);
        });

        try{
            adminService.updateAdminById(0L, 1L, adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("email");
            assertThat(e.getMessage()).isEqualTo("Field 'email' with value 'null' is invalid");
        }
    }
    @Test
    public void testUpdateAdminByIdNoName()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        adminRequestModel.setName("");

        assertThrows(InvalidFieldException.class, () -> {
            adminService.updateAdminById(0L, 1L, adminRequestModel);
            verify(adminRepository).findById(1L);
            verify(adminRepository).findById(0L);
        });

        try{
            adminService.updateAdminById(0L, 1L, adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("name");
            assertThat(e.getMessage()).isEqualTo("Field 'name' with value '' is invalid");
        }
    }
    @Test
    public void testUpdateAdminByIdNullName()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        adminRequestModel.setName(null);

        assertThrows(InvalidFieldException.class, () -> {
            adminService.updateAdminById(0L, 1L, adminRequestModel);
            verify(adminRepository).findById(1L);
            verify(adminRepository).findById(0L);
        });

        try{
            adminService.updateAdminById(0L, 1L, adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("name");
            assertThat(e.getMessage()).isEqualTo("Field 'name' with value 'null' is invalid");
        }
    }
    @Test
    public void testUpdateAdminByIdNoSurname()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        adminRequestModel.setSurname("");

        assertThrows(InvalidFieldException.class, () -> {
            adminService.updateAdminById(0L, 1L, adminRequestModel);
            verify(adminRepository).findById(1L);
            verify(adminRepository).findById(0L);
        });

        try{
            adminService.updateAdminById(0L, 1L, adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("surname");
            assertThat(e.getMessage()).isEqualTo("Field 'surname' with value '' is invalid");
        }
    }
    @Test
    public void testUpdateAdminByIdNullSurname()
            throws UserNotFoundException, NotAuthorizedException, MissingBodyException,
            InvalidFieldException {
        adminRequestModel.setSurname(null);

        assertThrows(InvalidFieldException.class, () -> {
            adminService.updateAdminById(0L, 1L, adminRequestModel);
            verify(adminRepository).findById(1L);
            verify(adminRepository).findById(0L);
        });

        try{
            adminService.updateAdminById(0L, 1L, adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("surname");
            assertThat(e.getMessage()).isEqualTo("Field 'surname' with value 'null' is invalid");
        }
    }
}
