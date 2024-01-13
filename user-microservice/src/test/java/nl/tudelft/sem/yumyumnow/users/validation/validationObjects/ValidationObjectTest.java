package nl.tudelft.sem.yumyumnow.users.validation.validationObjects;

import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationAdminOnlyStrategy;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.authorization.AuthorizationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class ValidationObjectTest {

    ValidationObject validationObject;
    UserRequestModel userRequestModel;
    AuthorizationData authorizationData;

    @BeforeEach
    void setUp(){
        UserRepository userRepository = mock(UserRepository.class);
        authorizationData = new AuthorizationData(1L, 1L, userRepository, new AuthorizationAdminOnlyStrategy());
        userRequestModel = new UserRequestModel("email", "name", "s");
        validationObject = new ValidationObject(userRequestModel, authorizationData);

    }
    @Test
    void getUserRequestModelTest(){
        assertThat(validationObject.getUserRequestModel()).isEqualTo(userRequestModel);
    }
    @Test
    void getAuthorizationDataTest(){
        assertThat(validationObject.getAuthorizationData()).isEqualTo(authorizationData);
    }
}
