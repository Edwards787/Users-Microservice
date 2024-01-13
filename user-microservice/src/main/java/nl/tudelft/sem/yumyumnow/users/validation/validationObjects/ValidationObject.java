package nl.tudelft.sem.yumyumnow.users.validation.validationObjects;

import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import nl.tudelft.sem.yumyumnow.users.validation.validationObjects.Authorization.AuthorizationData;

public class ValidationObject {

    private final UserRequestModel userRequestModel;
    private final AuthorizationData authorizationData;


    public ValidationObject(UserRequestModel userRequestModel, AuthorizationData authorizationData) {
        this.userRequestModel = userRequestModel;
        this.authorizationData = authorizationData;
    }

    public UserRequestModel getUserRequestModel() {
        return userRequestModel;
    }

    public AuthorizationData getAuthorizationData() {
        return authorizationData;
    }
}
