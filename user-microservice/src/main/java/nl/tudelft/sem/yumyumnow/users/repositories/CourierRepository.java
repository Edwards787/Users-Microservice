package nl.tudelft.sem.yumyumnow.users.repositories;

import nl.tudelft.sem.yumyumnow.users.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepository  extends JpaRepository<Courier, Long> {
    Optional<Object> findByEmail(String email);
}
