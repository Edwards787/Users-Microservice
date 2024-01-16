package nl.tudelft.sem.yumyumnow.users.services;


import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.yumyumnow.users.builders.email.Email;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class MockEmailService extends EmailService {

    @Override
    public void sendEmail(Email email) {
        log.info("Fake email sent to " + email.getRecipient() + ". Subject: " + email.getSubject()
                + ". Body: " + email.getBody());
    }

}
