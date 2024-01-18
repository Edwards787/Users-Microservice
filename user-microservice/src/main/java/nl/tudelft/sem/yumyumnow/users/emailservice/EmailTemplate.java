package nl.tudelft.sem.yumyumnow.users.emailservice;

public abstract class EmailTemplate {

    protected abstract String buildBody();

    protected abstract String buildSubject();

}
