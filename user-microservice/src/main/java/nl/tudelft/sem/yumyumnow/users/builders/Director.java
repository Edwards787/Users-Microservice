package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;

public class Director {
    public void constructCustomer(Builder builder, CustomerRequestModel crm){
        builder.setRole(User.RoleEnum.CUSTOMER).setName(crm.getName()).setSurname(crm.getSurname()).setEmail(crm.getEmail())
                .setBlockedStatus(false).setHomeAddress(crm.getHomeAddress()).setAllergens(crm.getAllergens());

    }
}
