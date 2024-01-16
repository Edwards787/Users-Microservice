package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import org.springframework.stereotype.Service;

@Service
public abstract class UserFactory implements Factory {
    public User build(UserRequestModel requestModel) {
        User user = new User();

        user.setEmail(requestModel.getEmail());
        user.setName(requestModel.getName());
        user.setSurname(requestModel.getSurname());
        user.setBlocked(false);

        return user;
    }

}
