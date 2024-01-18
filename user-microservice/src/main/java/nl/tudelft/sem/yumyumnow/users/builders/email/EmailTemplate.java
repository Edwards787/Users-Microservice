package nl.tudelft.sem.yumyumnow.users.builders.email;

public abstract class EmailTemplate {

    protected abstract String buildBody();

    protected abstract String buildSubject();

}
