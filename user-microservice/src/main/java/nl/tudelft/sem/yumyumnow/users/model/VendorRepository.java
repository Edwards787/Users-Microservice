package nl.tudelft.sem.yumyumnow.users.model;

import nl.tudelft.sem.yumyumnow.users.repositories.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository  extends JpaRepository<Vendor, Long> {
}
