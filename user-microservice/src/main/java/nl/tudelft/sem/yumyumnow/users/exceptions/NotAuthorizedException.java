package nl.tudelft.sem.yumyumnow.users.exceptions;

import java.io.Serial;

public class NotAuthorizedException extends Exception {

    @Serial
    private static final long serialVersionUID = -8652829625080878670L;

    public NotAuthorizedException() {
        super("Not authorized");
    }

    public String  customMessage(Long requester, String action, Long target) {
        return "User with id " + requester + " not authorized to " + action + " vendor with id " + target;
    }
}
