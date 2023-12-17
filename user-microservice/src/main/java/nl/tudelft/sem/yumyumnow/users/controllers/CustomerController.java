package nl.tudelft.sem.yumyumnow.users.controllers;

import nl.tudelft.sem.yumyumnow.users.api.CustomerApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.Courier;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.PaymentMethod;
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
        return customerService.createCustomer(customerRequestModel);
    }

    @Override
    public ResponseEntity<Void> deleteCustomer(Long userId1, Long userId2) {
        try {
            customerService.deleteCustomer(userId1, userId2);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (StatusException e) {
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<Customer>> getAllCustomers() {
        ResponseEntity<List<Customer>> responseEntity;
        try{
            responseEntity = customerService.getAllCustomers();
        }
        catch(StatusException e){
            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
        }
        return responseEntity;

    }
//TODO
    
//    @Override
//    public ResponseEntity<Location> getCurrentLocationForUser(Long userId) {
//        try {
//            Location location = customerService.getCurrentLocationForUser(userId);
//            return new ResponseEntity<>(location, HttpStatus.OK);
//        } catch (StatusException e) {
//            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
//        }
//    }
//
//    @Override
//    public ResponseEntity<List<Long>> getCurrentOrderIdsForUser(Long userId) {
//        try {
//            List<Long> orderIds = customerService.getCurrentOrderIdsForUser(userId);
//            return new ResponseEntity<>(orderIds, HttpStatus.OK);
//        } catch (StatusException e) {
//            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
//        }
//    }
//
//    @Override
//    public ResponseEntity<Customer> getCustomerById(Long userId) {
//        try {
//            Customer customer = customerService.getCustomerById(userId);
//            return new ResponseEntity<>(customer, HttpStatus.OK);
//        } catch (StatusException e) {
//            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
//        }
//    }
//
//    @Override
//    public ResponseEntity<PaymentMethod> getCustomerPaymentById(Long userId1, Long userId2) {
//        try {
//            PaymentMethod paymentMethod = customerService.getCustomerPaymentById(userId1, userId2);
//            return new ResponseEntity<>(paymentMethod, HttpStatus.OK);
//        } catch (StatusException e) {
//            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
//        }
//    }
//
//    @Override
//    public ResponseEntity<List<Long>> getFavouriteVendorIdsForCustomer(Long userId) {
//        try {
//            List<Long> favVendorIds = customerService.getFavouriteVendorIdsForCustomer(userId);
//            return new ResponseEntity<>(favVendorIds, HttpStatus.OK);
//        } catch (StatusException e) {
//            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
//        }
//    }
//
//    @Override
//    public ResponseEntity<Long> getPastSavedOrderIdForUser(Long userId) {
//        try {
//            Long orderId = customerService.getPastSavedOrderIdForUser(userId);
//            return new ResponseEntity<>(orderId, HttpStatus.OK);
//        } catch (StatusException e) {
//            throw new ResponseStatusException(e.getHttpStatus(), e.getMessage());
//        }
//    }
}
