package nl.tudelft.sem.yumyumnow.users.exceptions;

import java.io.Serial;

public class UserNotFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = 4958462377636536920L;

    private static final String defaultMessage = "User not found";

    public UserNotFoundException() {
        super(defaultMessage);
    }

}
