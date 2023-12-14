package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.repositories.CourierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourierService {
    private final CourierRepository courierRepository;

    @Autowired
    public CourierService(CourierRepository courierRepository){
        this.courierRepository = courierRepository;
    }
}
