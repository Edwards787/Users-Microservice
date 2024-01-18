package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.emailservice.Email;

public abstract class EmailService {

    public abstract void sendEmail(Email email);

}
