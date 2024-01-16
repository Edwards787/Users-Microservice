package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;

public class CustomerFactory extends UserFactory {
    /**
     * Builds a customer object (sets needed fields).
     *
     * @param customerRequestModel  the attributes that are being updated/posted
     *
     * @return return the updated Customer object
     */
    public Customer build(CustomerRequestModel customerRequestModel) {
        Customer c = (Customer) super.build(customerRequestModel);

        c.setRole(User.RoleEnum.CUSTOMER);
        c.setAllergens(customerRequestModel.getAllergens());
        c.setHomeAddress(customerRequestModel.getHomeAddress());

        return c;
    }
}