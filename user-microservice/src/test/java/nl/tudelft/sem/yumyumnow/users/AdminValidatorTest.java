package nl.tudelft.sem.yumyumnow.users;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AdminValidatorTest {
    AdminValidator adminValidator;
    AdminRequestModel adminRequestModel;
    @BeforeEach
    public void setUp(){
       adminValidator = new AdminValidator();
       adminRequestModel = new AdminRequestModel("email@mail.com", "name", "surname");
    }

    @Test
    public void testNoBody(){
        adminRequestModel = null;
        assertThrows(MissingBodyException.class, () -> {
            adminValidator.validate(adminRequestModel);
        });
    }
    @Test
    public void testNoName(){
        adminRequestModel.setName("");
        assertThrows(InvalidFieldException.class, () -> {
            adminValidator.validate(adminRequestModel);
        });

        try {
            adminValidator.validate(adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("name");
            assertThat(e.getMessage()).isEqualTo("Field 'name' with value '' is invalid");
        } catch (MissingBodyException e){
            assertThat(1).isEqualTo(0);
        }
    }
    @Test
    public void testNoEmail(){
        adminRequestModel.setEmail("");
        assertThrows(InvalidFieldException.class, () -> {
            adminValidator.validate(adminRequestModel);
        });

        try {
            adminValidator.validate(adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("email");
            assertThat(e.getMessage()).isEqualTo("Field 'email' with value '' is invalid");
        } catch (MissingBodyException e){
            assertThat(1).isEqualTo(0);
        }
    }
    @Test
    public void testNoSurname(){
        adminRequestModel.setSurname("");
        assertThrows(InvalidFieldException.class, () -> {
            adminValidator.validate(adminRequestModel);
        });

        try {
            adminValidator.validate(adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("surname");
            assertThat(e.getMessage()).isEqualTo("Field 'surname' with value '' is invalid");
        } catch (MissingBodyException e){
            assertThat(1).isEqualTo(0);
        }
    }
    @Test
    public void testNullName(){
        adminRequestModel.setName(null);
        assertThrows(InvalidFieldException.class, () -> {
            adminValidator.validate(adminRequestModel);
        });

        try {
            adminValidator.validate(adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("name");
            assertThat(e.getMessage()).isEqualTo("Field 'name' with value 'null' is invalid");
        } catch (MissingBodyException e){
            assertThat(1).isEqualTo(0);
        }
    }
    @Test
    public void testNullEmail(){
        adminRequestModel.setEmail(null);
        assertThrows(InvalidFieldException.class, () -> {
            adminValidator.validate(adminRequestModel);
        });

        try {
            adminValidator.validate(adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("email");
            assertThat(e.getMessage()).isEqualTo("Field 'email' with value 'null' is invalid");
        } catch (MissingBodyException e){
            assertThat(1).isEqualTo(0);
        }
    }
    @Test
    public void testNullSurname(){
        adminRequestModel.setSurname(null);
        assertThrows(InvalidFieldException.class, () -> {
            adminValidator.validate(adminRequestModel);
        });

        try {
            adminValidator.validate(adminRequestModel);
        } catch (InvalidFieldException e){
            assertThat(e.fieldName).isEqualTo("surname");
            assertThat(e.getMessage()).isEqualTo("Field 'surname' with value 'null' is invalid");
        } catch (MissingBodyException e){
            assertThat(1).isEqualTo(0);
        }
    }
}
