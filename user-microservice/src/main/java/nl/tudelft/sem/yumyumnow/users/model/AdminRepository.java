package nl.tudelft.sem.yumyumnow.users.model;

import nl.tudelft.sem.yumyumnow.users.repositories.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Object> findByEmail(String email);
}
