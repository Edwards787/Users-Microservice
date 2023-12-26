package nl.tudelft.sem.yumyumnow.users.exceptions;

import java.io.Serial;

public class NotAuthorizedException extends Exception {

    @Serial
    private static final long serialVersionUID = -8652829625080878670L;

    public NotAuthorizedException() {
        super("Not authorized");
    }

    public NotAuthorizedException(String message) {
        super(message);
    }
}
