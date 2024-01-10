package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.CustomerApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;

import nl.tudelft.sem.yumyumnow.users.model.*;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class CustomerController implements CustomerApi {


    private final CustomerService customerService;
    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public ResponseEntity<Customer> createCustomer(CustomerRequestModel customerRequestModel) {
        Customer c;
        try{
           c =  customerService.createCustomer(customerRequestModel);
        }
        catch (MissingBodyException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body not present");
        }
        catch(InvalidFieldException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok(c);
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(Long userId1, Long userId2) {
        try{
            customerService.deleteCustomer(userId1, userId2);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        } catch (InitialCustomerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Initial customer can not be deleted");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        }
    }

    @Override
    public ResponseEntity<List<Customer>> getAllCustomers() {
        ResponseEntity<List<Customer>> responseEntity;

            responseEntity = customerService.getAllCustomers();
        return responseEntity;

    }

    @Override
    public ResponseEntity<Customer> getCustomerById(Long userId) {
        try {
            Customer customer = customerService.getCustomerById(userId);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
    }

    @Override
    public ResponseEntity<Void> updateCustomer(Long userId1, Long userId2, CustomerRequestModel customerRequestModel){
        try{
            customerService.updateCustomerById(userId1, userId2, customerRequestModel);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(UserNotFoundException e) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        } catch (NotAuthorizedException e) {
        throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        } catch (InvalidFieldException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (MissingBodyException e) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body not present");
        }
}

    @Override
    public ResponseEntity<Location> getCurrentLocationForUser(Long id){
        try{
        Location l = customerService.getLocation(id);
        return new ResponseEntity<>(l, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
    }

    @Override
    public ResponseEntity<List<Long>> getFavouriteVendorIdsForCustomer(Long id){
        try{
            List<Long> favs = customerService.getFavoriteRestaurants(id);
            return new ResponseEntity<>(favs, HttpStatus.OK);
        }
        catch(UserNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
    }

    @Override
    public ResponseEntity<Void> updateFavouriteVendorIdsForCustomer(Long userId1, Long userId2, List<Long> favorites){
        try{
            customerService.updateFavRestaurants(userId1, userId2, favorites);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), "Unauthorized action");
        } catch (InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body not present");
        }
    }




}
