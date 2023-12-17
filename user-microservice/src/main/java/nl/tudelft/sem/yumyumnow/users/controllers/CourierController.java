package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.api.CourierApi;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
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
}
