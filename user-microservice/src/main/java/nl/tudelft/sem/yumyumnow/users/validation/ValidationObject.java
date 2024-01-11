package nl.tudelft.sem.yumyumnow.users.validation;

import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;

public class ValidationObject {

    private final UserRequestModel userRequestModel;
    private final Long id1;
    private final Long id2;

    public ValidationObject(UserRequestModel userRequestModel, Long id1, Long id2) {
        this.userRequestModel = userRequestModel;
        this.id1 = id1;
        this.id2 = id2;
    }

    public UserRequestModel getUserRequestModel() {
        return userRequestModel;
    }

    public Long getId1() {
        return id1;
    }

    public Long getId2() {
        return id2;
    }

}
