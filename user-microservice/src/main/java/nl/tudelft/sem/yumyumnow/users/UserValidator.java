package nl.tudelft.sem.yumyumnow.users;

import java.util.regex.Pattern;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import org.springframework.stereotype.Service;

@Service
public abstract class UserValidator {
    protected void validate(UserRequestModel userRequestModel)
            throws MissingBodyException, InvalidFieldException {

        if (userRequestModel == null) {
            throw new MissingBodyException();
        }

        if (userRequestModel.getName() == null || userRequestModel.getName().isEmpty()) {
            throw new InvalidFieldException("name", userRequestModel.getName());
        }
        if (userRequestModel.getEmail() == null
                || userRequestModel.getEmail().isEmpty()
                || !Pattern.compile("^(.+)@(\\S+)$")
                    .matcher(userRequestModel.getEmail())
                    .matches()) {
            throw new InvalidFieldException("email", userRequestModel.getEmail());
        }
        if (userRequestModel.getSurname() == null || userRequestModel.getSurname().isEmpty()) {
            throw new InvalidFieldException("surname", userRequestModel.getSurname());
        }

    }
}
