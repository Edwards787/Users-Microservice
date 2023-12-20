package nl.tudelft.sem.yumyumnow.users.validators;

import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import org.springframework.stereotype.Service;

@Service
public abstract class UserValidator {
    protected void validate(UserRequestModel userRequestModel)
        throws MissingBodyException, InvalidFieldException {

        if (userRequestModel == null)
            throw new MissingBodyException();

        if (userRequestModel.getName() == null || userRequestModel.getName().equals(""))
            throw new InvalidFieldException("name", userRequestModel.getName());
        if (userRequestModel.getEmail() == null || userRequestModel.getEmail().equals(""))
            throw new InvalidFieldException("email", userRequestModel.getEmail());
        if (userRequestModel.getSurname() == null || userRequestModel.getSurname().equals(""))
            throw new InvalidFieldException("surname", userRequestModel.getSurname());
    }
}
