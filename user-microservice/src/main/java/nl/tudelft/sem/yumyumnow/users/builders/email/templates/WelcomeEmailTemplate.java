package nl.tudelft.sem.yumyumnow.users.builders.email.templates;

import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.yumyumnow.users.builders.email.EmailTemplate;

@Getter
@Setter
public class WelcomeEmailTemplate extends EmailTemplate {

    private String name;

    public WelcomeEmailTemplate(String name) {
        this.name = name;
    }

    @Override
    protected String buildBody() {
        return String.format("Hi %s, you're YumYumNow account has been created", name);
    }

    @Override
    protected String buildSubject() {
        return "Welcome to YumYumNow!";
    }
}
