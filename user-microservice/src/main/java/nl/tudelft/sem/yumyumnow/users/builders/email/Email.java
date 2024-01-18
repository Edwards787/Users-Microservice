package nl.tudelft.sem.yumyumnow.users.builders.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Email {

    private String recipient;
    private String subject;
    private String body;

    protected Email(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

}
