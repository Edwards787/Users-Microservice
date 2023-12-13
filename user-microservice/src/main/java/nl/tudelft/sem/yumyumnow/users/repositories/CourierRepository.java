package nl.tudelft.sem.yumyumnow.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepository  extends JpaRepository<Courier, Long> {
}
