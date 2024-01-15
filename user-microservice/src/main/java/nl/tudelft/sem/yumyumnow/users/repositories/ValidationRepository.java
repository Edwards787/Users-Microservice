package nl.tudelft.sem.yumyumnow.users.repositories;

import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.model.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * A DDD repository for querying and persisting validation aggregate roots.
 */
@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {
    /**
     * Find validation by vendor id.
     */
    Optional<Validation> findById(Long vendorId);
}
