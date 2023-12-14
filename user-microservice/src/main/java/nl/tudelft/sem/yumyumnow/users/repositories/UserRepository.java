package nl.tudelft.sem.yumyumnow.users.repositories;

import java.util.Optional;

import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find user by id.
     */
    Optional<User> findById(Long id);

    /**
     * Check if an existing user already uses an id.
     */
    boolean existsById(Long id);
}
