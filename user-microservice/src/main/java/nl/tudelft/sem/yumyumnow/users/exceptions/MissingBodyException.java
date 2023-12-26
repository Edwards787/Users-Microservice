package nl.tudelft.sem.yumyumnow.users.exceptions;

import java.io.Serial;

public class MissingBodyException extends Exception {
    @Serial
    private static final long serialVersionUID = -763530046512662020L;

    public MissingBodyException() {
        super("Body is missing");
    }
}
