package nl.tudelft.sem.yumyumnow.users.validation.Objects;

import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;

public class AuthorizationObject {

    private final Long id1;
    private final Long id2;
    private final UserRepository userRepository;
    private final Accessibility accessibility;

    public AuthorizationObject(Long id1, Long id2, UserRepository userRepository, Accessibility accessibility) {
        this.id1 = id1;
        this.id2 = id2;
        this.userRepository = userRepository;
        this.accessibility = accessibility;
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

    public Accessibility getAccessibility() {
        return accessibility;
    }
}
