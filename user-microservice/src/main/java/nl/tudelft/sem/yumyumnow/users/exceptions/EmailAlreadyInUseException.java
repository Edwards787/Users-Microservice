package nl.tudelft.sem.yumyumnow.users.exceptions;


import java.io.Serial;

public class EmailAlreadyInUseException extends Exception {

    @Serial
    private static final long serialVersionUID = 6731200478101118320L;

    private static final String formatString = "User with email '%s' already exists";

    public final String email;

    public EmailAlreadyInUseException(String email) {
        super(String.format(formatString, email));
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }
}
