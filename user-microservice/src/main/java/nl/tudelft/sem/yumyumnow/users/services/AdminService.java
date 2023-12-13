package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.repositories.Admin;
import nl.tudelft.sem.yumyumnow.users.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    @Autowired
    public AdminService(AdminRepository adminRepository){
        this.adminRepository = adminRepository;
    }

    public ResponseEntity<Admin> getAdminById(Long adminId){
        Optional<Admin> admin = adminRepository.findById(adminId);

        if (admin.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else return ResponseEntity.status(HttpStatus.OK).body(admin.get());
    }
}