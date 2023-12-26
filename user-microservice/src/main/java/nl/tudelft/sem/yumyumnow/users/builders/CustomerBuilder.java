package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;

public class CustomerBuilder extends UserBuilder {
    /**
     * Builds an customer object (sets needed fields).
     *
     * @param customer              initial customer object
     * @param customerRequestModel  the attributes that are being updated/posted
     *
     * @return return the updated Customer object
     */
    public Customer build(Customer customer, CustomerRequestModel customerRequestModel) {
        super.build(customer, customerRequestModel);
        customer.setRole(User.RoleEnum.CUSTOMER);
        return customer;
    }
}