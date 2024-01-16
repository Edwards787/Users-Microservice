package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;

public interface Factory {
    User build(UserRequestModel userRequestModel);
}
