package nl.tudelft.sem.yumyumnow.users.builders.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailBuilder {

    private String recipient;
    private EmailTemplate template;

    public EmailBuilder() {
    }

    public EmailBuilder setRecipient(String recipient) {
        this.recipient = recipient;
        return this;
    }

    public EmailBuilder setTemplate(EmailTemplate template) {
        this.template = template;
        return this;
    }

    public Email build() {
        return new Email(recipient, template.buildSubject(), template.buildBody());
    }

}
