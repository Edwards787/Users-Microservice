package nl.tudelft.sem.yumyumnow.users.validation.objects.authorization;

import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class AuthorizationDataTest {

    transient Long id1;
    transient Long id2;
    transient UserRepository userRepository;
    transient AuthorizationStrategy authorizationStrategy;
    transient AuthorizationData authorizationData;

    @BeforeEach
    void setUp(){
       id1 = 1L;
       id2 = 2L;
       userRepository = mock(UserRepository.class);
       authorizationStrategy = new AuthorizationAdminOnlyStrategy();

       authorizationData = new AuthorizationData(id1, id2, userRepository, authorizationStrategy);
    }

    @Test
    void testGetId1(){
        assertThat(authorizationData.getId1()).isEqualTo(id1);
    }
    @Test
    void testGetId2(){
        assertThat(authorizationData.getId2()).isEqualTo(id2);
    }
    @Test
    void testGetUserRepository(){
        assertThat(authorizationData.getUserRepository()).isEqualTo(userRepository);
    }
    @Test
    void testGetAuthorizationStrategy(){
        assertThat(authorizationData.getAuthorizationStrategy()).isEqualTo(authorizationStrategy);
    }
}
