package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import org.springframework.stereotype.Service;

@Service
public abstract class UserBuilder {

    protected User build(User user, UserRequestModel requestModel) {
        user.email(requestModel.getEmail());
        user.name(requestModel.getName());
        user.surname(requestModel.getSurname());
        if (user.getBlocked() == null) {
            user.blocked(false);
        }
        return user;
    }
}
