package nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.Authorization;

import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;

public class AuthorizationData {

    private final Long id1;
    private final Long id2;
    private final UserRepository userRepository;
    private final AuthorizationStrategy authorizationStrategy;

    public AuthorizationData(Long id1, Long id2, UserRepository userRepository, AuthorizationStrategy authorizationStrategy) {
        this.id1 = id1;
        this.id2 = id2;
        this.userRepository = userRepository;
        this.authorizationStrategy = authorizationStrategy;
    }

    public Long getId1() {
        return id1;
    }

    public Long getId2() {
        return id2;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public AuthorizationStrategy getAuthorizationStrategy() {
        return authorizationStrategy;
    }
}
