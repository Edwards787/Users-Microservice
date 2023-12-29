package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import org.springframework.stereotype.Service;

@Service
public class CustomerValidator extends UserValidator{
    public void validate(CustomerRequestModel customerRequestModel) throws MissingBodyException, InvalidFieldException {
        super.validate(customerRequestModel);
        if(customerRequestModel.getAllergens() == null){
            throw new InvalidFieldException("allergens", customerRequestModel.getAllergens().toString());
        }
        if(customerRequestModel.getHomeAddress() == null){
            throw new InvalidFieldException("address", customerRequestModel.getHomeAddress().getAddress());
        }
    }
}