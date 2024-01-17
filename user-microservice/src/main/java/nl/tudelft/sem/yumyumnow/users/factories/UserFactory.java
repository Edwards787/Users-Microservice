package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import org.springframework.stereotype.Component;

@Component
public interface UserFactory<U extends User, R extends UserRequestModel> {
    U build(R userRequestModel);
}
