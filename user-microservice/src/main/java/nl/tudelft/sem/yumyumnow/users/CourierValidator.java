package nl.tudelft.sem.yumyumnow.users;
import nl.tudelft.sem.yumyumnow.users.exceptions.InvalidFieldException;
import nl.tudelft.sem.yumyumnow.users.exceptions.MissingBodyException;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import org.springframework.stereotype.Service;

@Service
public class CourierValidator extends UserValidator{
    public void validate(CourierRequestModel courierRequestModel) throws MissingBodyException, InvalidFieldException {
        super.validate(courierRequestModel);
    }
}
