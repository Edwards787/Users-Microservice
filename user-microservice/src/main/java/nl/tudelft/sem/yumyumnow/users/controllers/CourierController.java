package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.api.CourierApi;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Rating;
import nl.tudelft.sem.yumyumnow.users.services.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
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
        ResponseEntity<Courier> responseEntity;
        try{
            responseEntity = courierService.getCourierById(userId);
        }
        catch (StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<Courier> createCourier(CourierRequestModel courierRequestModel){
        ResponseEntity<Courier> responseEntity;
        try{
            responseEntity = courierService.createCourier(courierRequestModel);
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<List<Courier>> getAllCouriers(){
        ResponseEntity<List<Courier>> responseEntity;
        try{
            responseEntity = courierService.getAllCouriers();
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<Void> deleteCourier(Long userId1, Long userId2){
        ResponseEntity<Void> responseEntity;
        try{
            responseEntity = courierService.deleteCourier(userId1 , userId2);
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<Void> updateCourier(Long userId1, Long userId2, Courier courier){
        ResponseEntity<Void> responseEntity;
        try{
            responseEntity = courierService.updateCourier(userId1 , userId2, courier);
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<List<Long>> getPastOrderIdsForCourier(Long userId){
        //TODO : MAKE SURE TO FIX THIS WHEN WE DECIDE HOW TO GET ORDERS
        ResponseEntity<List<Long>> responseEntity;
        try{
            responseEntity = courierService.getPastOrderIdsForCourier(userId);
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<List<List<Rating>>> getRatingsForCourier(Long userId){
        //TODO : IMPLEMENT THIS WHEN U TALK TO TEAM
        ResponseEntity<List<List<Rating>>> responseEntity;
        try{
            responseEntity = courierService.getRatingsForCourier(userId);
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<Void> updateRatingsForCourier(Long userId1, Long userId2, Rating rating){
        //TODO : IMPLEMENT THIS WHEN U TALK TO TEAM
        ResponseEntity<Void> responseEntity;
        try{
            responseEntity = courierService.updateRatingsForCourier(userId1, userId2, rating);
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<Void> updateCourierActivationStatus(Long userId1, Long userId2, Boolean body){
        ResponseEntity<Void> responseEntity;
        try{
            responseEntity = courierService.updateCourierActivationStatus(userId1, userId2, body);
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<Void> updateCourierBlockStatus(Long userId1, Long userId2, Boolean body){
        ResponseEntity<Void> responseEntity;
        try{
            responseEntity = courierService.updateCourierBlockStatus(userId1, userId2, body);
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;
    }
}
