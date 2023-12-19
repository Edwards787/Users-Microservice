package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.StatusException;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminValidator {
    public void validate(AdminRequestModel adminRequestModel){
        if (adminRequestModel == null)
            throw new StatusException(HttpStatus.BAD_REQUEST, "No admin request model present");

        if (adminRequestModel.getSurname().equals("") || adminRequestModel.getSurname() == null)
            throw new StatusException(HttpStatus.BAD_REQUEST, "No surname provided");
    }
}
