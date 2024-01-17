package nl.tudelft.sem.yumyumnow.users.factories;

import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.CustomerRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import org.springframework.stereotype.Component;

@Component
public class CustomerFactory implements UserFactory<Customer, CustomerRequestModel> {
    /**
     * Builds a customer object (sets needed fields).
     *
     * @param customerRequestModel  the attributes that are being updated/posted
     *
     * @return return the updated Customer object
     */
    public Customer build(CustomerRequestModel customerRequestModel) {
        Customer c = new Customer();

        c.setName(customerRequestModel.getName());
        c.setSurname(customerRequestModel.getSurname());
        c.setEmail(customerRequestModel.getEmail());
        c.setRole(User.RoleEnum.CUSTOMER);
        c.setAllergens(customerRequestModel.getAllergens());
        c.setHomeAddress(customerRequestModel.getHomeAddress());
        c.setBlocked(false);

        return c;
    }
}