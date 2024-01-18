package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.builders.email.Email;

public abstract class EmailService {

    public abstract void sendEmail(Email email);

}
