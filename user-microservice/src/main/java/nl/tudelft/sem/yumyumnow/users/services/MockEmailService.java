package nl.tudelft.sem.yumyumnow.users.services;


import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.yumyumnow.users.builders.email.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class MockEmailService extends EmailService {

    static final Logger log = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void sendEmail(Email email) {
        log.info("Fake email sent to " + email.getRecipient() + ". Subject: " + email.getSubject()
                + ". Body: " + email.getBody());
    }

}
