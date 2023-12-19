package nl.tudelft.sem.yumyumnow.users.controllers.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import org.springframework.stereotype.Service;

@Service
public class AdminValidator extends  UserValidator{
    public void validate(AdminRequestModel adminRequestModel) throws MissingBodyException, InvalidFieldException {
        super.validate(adminRequestModel);
    }
}
