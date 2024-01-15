package nl.tudelft.sem.yumyumnow.users.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ContentNotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = 4169405661066084090L;

    public ContentNotFoundException() {
        super("Requested content not found");
    }

    public ContentNotFoundException(String message) {
        super(message);
    }
}