package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.builders.AdminBuilder;
import nl.tudelft.sem.yumyumnow.users.exceptions.EmailAlreadyInUseException;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.exceptions.UserNotFoundException;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import nl.tudelft.sem.yumyumnow.users.services.AdminService;
import nl.tudelft.sem.yumyumnow.users.validators.AdminValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AdminServiceTest {
    private  AdminRepository adminRepository;
    private AdminValidator adminValidator;
    private AdminBuilder adminBuilder;
    private AdminService adminService;
    private Admin admin1;
    private AdminRequestModel adminRequestModel;

    @BeforeEach
    public void setUp(){
        adminRepository = mock(AdminRepository.class);
        adminValidator = mock(AdminValidator.class);
        adminBuilder = mock(AdminBuilder.class);

        adminService = new AdminService(adminRepository, adminBuilder, adminValidator);
    }

    @BeforeEach
    public void setUpEntities(){
        admin1 = new Admin();
        admin1.setSurname("surname 1");
        admin1.setName("name 1");
        admin1.setEmail("email 1");
        admin1.setRole(User.RoleEnum.ADMIN);
        admin1.setId(0L);

        adminRequestModel = new AdminRequestModel("email 1", "name 1", "surname 1");
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
        throws MissingBodyException, InvalidFieldException, EmailAlreadyInUseException {
        when(adminRepository.findByEmail("email 1")).thenReturn(Optional.empty());
        when(adminRepository.saveAndFlush(admin1)).thenReturn(admin1);
        assertThat(adminService.createAdmin(adminRequestModel)).isEqualTo(admin1);
    }


}
