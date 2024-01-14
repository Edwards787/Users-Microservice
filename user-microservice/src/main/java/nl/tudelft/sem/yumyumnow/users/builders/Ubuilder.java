package nl.tudelft.sem.yumyumnow.users.builders;

import java.util.List;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.Customer;
import nl.tudelft.sem.yumyumnow.users.model.User;

public class Ubuilder implements Builder {
    private transient User.RoleEnum role;
    private transient String name;


    private transient String surname;
    private transient String email;
    private transient boolean blocked;
    private transient List<String> allergens;
    private transient Address homeAddress;

    public void setRole(User.RoleEnum role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }

    @Override
    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Override
    public User build(User u) {
        if (role.equals(User.RoleEnum.CUSTOMER)) {
            Customer c = (Customer) u;
            c.setRole(role);
            c.setName(name);
            c.setSurname(surname);
            c.setEmail(email);
            c.setAllergens(allergens);
            c.setHomeAddress(homeAddress);
            c.setBlocked(blocked);
            return c;
        }
        User c = u;
        c.setRole(role);
        c.setName(name);
        c.setSurname(surname);
        c.setEmail(email);
        c.setBlocked(blocked);
        return c;
    }
}
