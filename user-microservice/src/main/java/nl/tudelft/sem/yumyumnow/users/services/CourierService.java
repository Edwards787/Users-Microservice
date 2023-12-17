package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Wallet;
import nl.tudelft.sem.yumyumnow.users.repositories.CourierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourierService {
    private final CourierRepository courierRepository;

    @Autowired
    public CourierService(CourierRepository courierRepository){
        this.courierRepository = courierRepository;
    }

    public ResponseEntity<Courier> getCourierById(Long courierId){
        Optional<Courier> courier = courierRepository.findById(courierId);

        if(courier.isEmpty()) throw new StatusException(HttpStatus.NOT_FOUND, "No courier with such id");
        else return ResponseEntity.status(HttpStatus.OK).body(courier.get());
    }

    public ResponseEntity<Courier> createCourier(CourierRequestModel courierRequestModel){
        if(courierRequestModel == null){
            throw new StatusException(HttpStatus.BAD_REQUEST, "No Courier Request model present");
        }
        if (courierRequestModel.getEmail().isEmpty()){
            throw new StatusException(HttpStatus.BAD_REQUEST, "No Email provided");
        }
        if (courierRequestModel.getName().isEmpty()){
            throw new StatusException(HttpStatus.BAD_REQUEST, "No Name provided");
        }
        if(courierRequestModel.getSurname().isEmpty()){
            throw new StatusException(HttpStatus.BAD_REQUEST, "No Surname provided");
        }
        if (courierRequestModel.getIbanNumber().isEmpty()){
            throw new StatusException(HttpStatus.BAD_REQUEST, "No Iban provided");
        }
        if(courierRepository.findByEmail(courierRequestModel.getEmail()).isPresent()){
            throw new StatusException(HttpStatus.CONFLICT, "Account with Email already exists");
        }

        Courier courier = new Courier();
        courier.setActivated(false);
        courier.setName(courierRequestModel.getName());
        courier.setEmail(courierRequestModel.getEmail());
        courier.setSurname(courierRequestModel.getSurname());
        Wallet wallet = new Wallet();
        wallet.setBalance(0f);
        wallet.setIban(courierRequestModel.getIbanNumber());
        courier.setWallet(wallet);

        Courier response = courierRepository.saveAndFlush(courier);
        courier.setId(response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(courier);

    }
}
