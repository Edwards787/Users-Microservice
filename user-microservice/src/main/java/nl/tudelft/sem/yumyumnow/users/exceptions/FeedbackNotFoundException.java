package nl.tudelft.sem.yumyumnow.users.exceptions;

import java.io.Serial;

public class FeedbackNotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = 3787350472101318320L;
    private final String message;

    public FeedbackNotFoundException(Long id) {
       this.message = "Feedback with id " + id.toString() + " not found!";
    }
    public String getMessage(){
        return this.message;
    }
}
