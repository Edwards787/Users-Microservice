package nl.tudelft.sem.yumyumnow.users.emailService;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EmailTest {

    Email email = new Email("recipient", "subject", "body");

    @Test
    void testGetRecipient() {
        assertThat(email.getRecipient()).isEqualTo("recipient");
    }

    @Test
    void testGetSubject() {
        assertThat(email.getSubject()).isEqualTo("subject");
    }

    @Test
    void testGetBody() {
        assertThat(email.getBody()).isEqualTo("body");
    }
}
