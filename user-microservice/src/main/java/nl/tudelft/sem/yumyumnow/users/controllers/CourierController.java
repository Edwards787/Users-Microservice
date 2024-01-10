package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.api.CourierApi;
import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.services.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class CourierController implements CourierApi {
    private final CourierService courierService;
    @Autowired
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @Override
    public ResponseEntity<Courier> getCourierById(Long userId){
        Courier courier;
        try {
            courier = courierService.getCourierById(userId);
            return ResponseEntity.ok(courier);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier not found");
        }
    }

    @Override
    public ResponseEntity<Courier> createCourier(CourierRequestModel courierRequestModel){
        Courier courier;
        try {
            courier = courierService.createCourier(courierRequestModel);
            return ResponseEntity.ok(courier);
        } catch (MissingBodyException | InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EmailAlreadyInUseException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<Courier>> getAllCouriers(){
        List<Courier> couriers;
        try{
            couriers = courierService.getAllCouriers();
            return ResponseEntity.ok(couriers);
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Something went wrong");
        }
    }

    @Override
    public ResponseEntity<Void> deleteCourier(Long userId1, Long userId2){
        try{
            courierService.deleteCourier(userId1 , userId2);
            return ResponseEntity.ok().build();
        }
        catch(UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Courier with ID " + userId1 + " not found");
        }
        catch (NotAuthorizedException e){
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");

        }
    }

    @Override
    public ResponseEntity<Void> updateCourier(Long userId1, Long userId2, CourierRequestModel courierRequestModel){
        try{
            courierService.updateCourier(userId1 , userId2, courierRequestModel);
            return ResponseEntity.ok().build();
        }
        catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId1 + " Not found");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        }
    }

    @Override
    public ResponseEntity<List<Long>> getPastOrderIdsForCourier(Long userId){
        //TODO : MAKE SURE TO ADD THE MOCKING OF CALLING AN API ENDPOINT
        //RIGHT NOW I JUST RETURN RANDOM NUMBERS
        List<Long> pastOrders;
        try{
            pastOrders = courierService.getPastOrderIdsForCourier(userId);
            return ResponseEntity.ok().body(pastOrders);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " Not found");
        }
    }

    @Override
    public ResponseEntity<List<Rating>> getRatingsForCourier(Long userId){
        List<Rating> ratings;
        try{
            ratings = courierService.getRatingsForCourier(userId);
            return ResponseEntity.ok().body(ratings);
        }
        catch(UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " Not found");
        }
    }

    @Override
    public ResponseEntity<Void> updateRatingsForCourier(Long userId1, Long userId2, List<Rating> ratings){
        try{
            courierService.updateRatingsForCourier(userId1, userId2, ratings);
            return ResponseEntity.ok().build();
        }
        catch (UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId1 + " Not found");
        } catch (NotAuthorizedException e){
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        }
    }

    @Override
    public ResponseEntity<Void> updateCourierActivationStatus(Long userId1, Long userId2, Boolean body){
        try{
            courierService.updateCourierActivationStatus(userId1, userId2, body);
            return ResponseEntity.ok().build();
        }
        catch(UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId1 + " Not found");
        }
        catch(NotAuthorizedException e){
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        }
    }

    @Override
    public ResponseEntity<Void> updateCourierBlockStatus(Long userId1, Long userId2, Boolean body) {
        try {
            courierService.updateCourierBlockStatus(userId1, userId2, body);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId1 + " Not found");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        }
    }
}
