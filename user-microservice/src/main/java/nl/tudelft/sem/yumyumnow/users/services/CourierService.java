package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.Wallet;
import nl.tudelft.sem.yumyumnow.users.repositories.CourierRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourierService {
    private final CourierRepository courierRepository;
    private final UserRepository userRepository;
    @Autowired
    public CourierService(CourierRepository courierRepository, UserRepository userRepository){
        this.courierRepository = courierRepository;
        this.userRepository = userRepository;
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

    public ResponseEntity<List<Courier>> getAllCouriers(){
        List<Courier> couriers = courierRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(couriers);
    }

    public ResponseEntity<Void> deleteCourier(Long userId1, Long userId2){
        Optional<Courier> courier = courierRepository.findById(userId1);

        if(courier.isEmpty()) throw new StatusException(HttpStatus.NOT_FOUND, "No courier with such id");
        Courier to_be_deleted = courier.get();
        if(to_be_deleted.getId().equals(userId2)){
            courierRepository.deleteById(userId1);
            courierRepository.flush();
            return ResponseEntity.ok().build();
        }

        Optional<User> admin = userRepository.findById(userId2);
        if(admin.isEmpty()){
            throw new StatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED need to be user or admin");
        }
        //now check if the user requesting is an admin
        User usr = admin.get();
        if(usr.getRole().equals(User.RoleEnum.ADMIN)){
            courierRepository.deleteById(userId1);
            courierRepository.flush();
            return ResponseEntity.ok().build();
        }
        throw new StatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED need to be user or admin");
    }
}
