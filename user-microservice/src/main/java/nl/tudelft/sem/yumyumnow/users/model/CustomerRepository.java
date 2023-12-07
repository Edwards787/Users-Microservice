package nl.tudelft.sem.yumyumnow.users.model;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    /**
     * Find user by id.
     */
    Optional<Customer> findById(Long id);

    /**
     * Check if an existing user already uses an id.
     */
    boolean existsById(Long id);
}
