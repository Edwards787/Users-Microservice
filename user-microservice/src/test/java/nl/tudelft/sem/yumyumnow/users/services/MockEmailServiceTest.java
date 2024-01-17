package nl.tudelft.sem.yumyumnow.users.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import nl.tudelft.sem.yumyumnow.users.builders.email.Email;
import nl.tudelft.sem.yumyumnow.users.builders.email.EmailBuilder;
import nl.tudelft.sem.yumyumnow.users.builders.email.templates.WelcomeEmailTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class MockEmailServiceTest {

    transient MockEmailService emailService;
    transient ListAppender<ILoggingEvent> emailServiceLogs;

    @BeforeEach
    void setUp() {
        emailServiceLogs = new ListAppender<>();
        emailServiceLogs.start();
        
        Logger emailServiceLogger = (Logger) LoggerFactory.getLogger(MockEmailService.class);
        emailServiceLogger.addAppender(emailServiceLogs);

        emailService = new MockEmailService();
    }

    @Test
    public void testSendEmail() {
        String recipient = "joe@example.com";
        String name = "Jane";
        Email email = new EmailBuilder()
                .setRecipient(recipient)
                .setTemplate(new WelcomeEmailTemplate(name))
                .build();

        emailService.sendEmail(email);

        List<ILoggingEvent> logs = emailServiceLogs.list;
        assertEquals(1, logs.size());
        assertEquals(Level.INFO, logs.get(0).getLevel());
        assertTrue(logs.get(0)
                .getMessage().toLowerCase()
                .contains("email"));
        assertTrue(logs.get(0)
                .getMessage()
                .contains(recipient));
    }

}
