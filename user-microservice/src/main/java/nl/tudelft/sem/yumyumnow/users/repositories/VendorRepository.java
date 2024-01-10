package nl.tudelft.sem.yumyumnow.users.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.yumyumnow.users.model.Vendor;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository  extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findById(Long id);

    Optional<Vendor> findByEmail(String email);

    List<Vendor> findAll();

    void deleteById(Long id);
}
