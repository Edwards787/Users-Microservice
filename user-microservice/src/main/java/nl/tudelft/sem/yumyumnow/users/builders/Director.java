package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.AdminRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.CourierRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;

public class Director {
    Builder builder;

    /**
     * Customer builder constructor.
     *
     * @param builder builder
     * @param crm     the customer request model
     */
    public void constructCustomer(Builder builder, CustomerRequestModel crm) {
        builder.setRole(User.RoleEnum.CUSTOMER);
        builder.setName(crm.getName());
        builder.setSurname(crm.getSurname());
        builder.setEmail(crm.getEmail());
        builder.setHomeAddress(crm.getHomeAddress());
        builder.setAllergens(crm.getAllergens());
        builder.setBlocked(false);
    }

    /**
     * Courier builder constructor.
     *
     * @param builder builder
     * @param crm     the courier request model
     */
    public void constructCourier(Builder builder, CourierRequestModel crm) {
        builder.setRole(User.RoleEnum.COURIER);
        builder.setName(crm.getName());
        builder.setSurname(crm.getSurname());
        builder.setEmail(crm.getEmail());
    }

    /**
     * Admin builder constructor.
     *
     * @param builder            builder
     * @param adminRequestModelm the admin request model
     */
    public void constructAdmin(Builder builder, AdminRequestModel adminRequestModelm) {
        builder.setRole(User.RoleEnum.ADMIN);
        builder.setName(adminRequestModelm.getName());
        builder.setSurname(adminRequestModelm.getSurname());
        builder.setEmail(adminRequestModelm.getEmail());
    }

}
