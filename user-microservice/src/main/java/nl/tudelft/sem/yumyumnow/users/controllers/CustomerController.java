package nl.tudelft.sem.yumyumnow.users.controllers;

import java.util.List;
import nl.tudelft.sem.yumyumnow.users.api.CustomerApi;
import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.Location;
import nl.tudelft.sem.yumyumnow.users.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class CustomerController implements CustomerApi {
    private final transient CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public ResponseEntity<Customer> createCustomer(CustomerRequestModel customerRequestModel) {
        try {
            Customer c = customerService.createCustomer(customerRequestModel);
            return ResponseEntity.ok(c);
        } catch (MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body not present");
        } catch (InvalidFieldException | UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (EmailAlreadyInUseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not authorized to create");
        }

    }

    @Override
    public ResponseEntity<Void> deleteCustomer(Long userId1, Long userId2) {
        try {
            customerService.deleteCustomer(userId1, userId2);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), e.getMessage());
        } catch (MissingBodyException | InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers;
        try {
            customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Something went wrong");
        }
    }

    @Override
    public ResponseEntity<Customer> getCustomerById(Long userId) {
        Customer customer;
        try {
            customer = customerService.getCustomerById(userId);
            return ResponseEntity.ok(customer);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        }
    }

    @Override
    public ResponseEntity<Void> updateCustomer(Long userId1, Long userId2, CustomerRequestModel customerRequestModel) {
        try {
            customerService.updateCustomerById(userId1, userId2, customerRequestModel);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), e.getMessage());
        } catch (InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body not present");
        }
    }

    @Override
    public ResponseEntity<Location> getCurrentLocationForUser(Long id) {
        try {
            Location l = customerService.getLocation(id);
            return new ResponseEntity<>(l, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        }
    }

    @Override
    public ResponseEntity<List<Long>> getFavouriteVendorIdsForCustomer(Long id) {
        try {
            List<Long> favs = customerService.getFavoriteRestaurants(id);
            return new ResponseEntity<>(favs, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        }
    }

    @Override
    public ResponseEntity<Long> getPastSavedOrderIdForUser(Long id) {
        try {
            Long orderId = customerService.getPastSavedOrderIdForUser(id);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        }
    }

    @Override
    public ResponseEntity<Void> updateFavouriteVendorIdsForCustomer(Long userId1, Long userId2, List<Long> favorites) {
        try {
            customerService.updateFavRestaurants(userId1, userId2, favorites);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), e.getMessage());
        } catch (MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body not present");
        } catch (InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "something went wrong");
        }
    }

    @Override
    public ResponseEntity<Void> updatePastSavedOrderIdForUser(Long userId1, Long userId2, Long orderId) {
        try {
            customerService.updatePastSavedOrderIdForUser(userId1, userId2, orderId);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), e.getMessage());
        } catch (MissingBodyException | InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "something went wrong");
        }
    }

    @Override
    public ResponseEntity<Void> updateCustomerBlockStatus(Long userId1, Long userId2, Boolean body) {
        try {
            customerService.updateCustomerBlockStatus(userId1, userId2, body);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId1 + " Not found");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), e.getMessage());
        } catch (InvalidFieldException | MissingBodyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<Long>> getCurrentOrderIdsForUser(Long userId) {
        try {
            List<Long> ids = customerService.getCurrentOrderIdsForUser(userId);
            return ResponseEntity.ok(ids);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getCustomMessage());
        }
    }

    @Override
    public ResponseEntity<Void> requestRefundByCustomerIdForOrderId(Long userId1, Long userId2, Long orderId) {
        try {
            customerService.requestRefundByCustomerIdForOrderId(userId1, userId2, orderId);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId1 + " Not found");
        } catch (NotAuthorizedException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(403), e.getMessage());
        } catch (MissingBodyException | InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
