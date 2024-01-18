package nl.tudelft.sem.yumyumnow.users.repositories;

import nl.tudelft.sem.yumyumnow.users.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
