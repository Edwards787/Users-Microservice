package nl.tudelft.sem.yumyumnow.users.repositories;

import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findById(Long id);

    Optional<Admin> findByEmail(String email);
}
