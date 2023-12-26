package nl.tudelft.sem.yumyumnow.users.exceptions;

import java.io.Serial;
import lombok.Getter;

@Getter
public class UserNotFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = 4958462377636536920L;

    private static final String defaultMessage = "User not found";

    private Long targetId;

    public UserNotFoundException() {
        super(defaultMessage);
    }

    public UserNotFoundException(Long targetId) {
        super(defaultMessage);
        this.targetId = targetId;
    }
}
