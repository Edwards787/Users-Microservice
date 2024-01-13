package nl.tudelft.sem.yumyumnow.users.repositories;

import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepository  extends JpaRepository<Courier, Long> {
    Optional<Object> findByEmail(String email);
}
