package nl.tudelft.sem.yumyumnow.users.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {


    Optional<Admin> findById(Long id);
}