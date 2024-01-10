package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;

public class Director {
    Builder builder;


    public void constructCustomer(Builder builder, CustomerRequestModel crm){
        builder.setRole(User.RoleEnum.CUSTOMER);
        builder.setName(crm.getName());
        builder.setSurname(crm.getSurname());
        builder.setEmail(crm.getEmail());
        builder.setHomeAddress(crm.getHomeAddress());
        builder.setAllergens(crm.getAllergens());
        builder.setBlocked(false);
    }
}
