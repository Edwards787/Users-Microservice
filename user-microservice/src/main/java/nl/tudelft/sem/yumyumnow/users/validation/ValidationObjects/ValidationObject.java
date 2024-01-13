package nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects;

import nl.tudelft.sem.yumyumnow.users.model.UserRequestModel;
import nl.tudelft.sem.yumyumnow.users.validation.ValidationObjects.Authorization.AuthorizationObject;

public class ValidationObject {

    private final UserRequestModel userRequestModel;
    private final AuthorizationObject authorizationObject;


    public ValidationObject(UserRequestModel userRequestModel, AuthorizationObject authorizationObject) {
        this.userRequestModel = userRequestModel;
        this.authorizationObject = authorizationObject;
    }

    public UserRequestModel getUserRequestModel() {
        return userRequestModel;
    }

    public AuthorizationObject getAuthorizationObject() {
        return authorizationObject;
    }
}
