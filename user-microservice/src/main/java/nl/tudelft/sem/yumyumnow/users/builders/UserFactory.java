package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import org.springframework.stereotype.Service;

@Service
public abstract class UserFactory implements Factory {
    public User build(User user, UserRequestModel requestModel) {
        user.setEmail(requestModel.getEmail());
        user.setName(requestModel.getName());
        user.setSurname(requestModel.getSurname());
        if (user.getBlocked() == null) {
            user.setBlocked(false);
        }
        return user;
    }

}
