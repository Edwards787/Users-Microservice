package nl.tudelft.sem.yumyumnow.users.emailService;

public abstract class EmailTemplate {

    protected abstract String buildBody();

    protected abstract String buildSubject();

}
