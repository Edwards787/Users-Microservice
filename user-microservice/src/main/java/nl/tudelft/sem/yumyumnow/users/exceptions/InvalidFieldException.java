package nl.tudelft.sem.yumyumnow.users.exceptions;

import java.io.Serial;

public class InvalidFieldException extends Exception {

    @Serial
    private static final long serialVersionUID = 2469943609539837807L;

    private static final String formatString = "Field '%s' with value '%s' is invalid";

    public final String fieldName;

    public InvalidFieldException(String fieldName, String value) {
        super(String.format(formatString, fieldName, value));
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

}
