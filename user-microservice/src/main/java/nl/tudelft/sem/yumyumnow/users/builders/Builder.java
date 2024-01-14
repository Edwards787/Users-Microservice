package nl.tudelft.sem.yumyumnow.users.builders;

import java.util.List;
import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.User;

public interface Builder {
    void setName(String name);

    void setSurname(String surname);

    void setEmail(String email);

    void setBlocked(boolean blocked);

    void setRole(User.RoleEnum role);

    void setAllergens(List<String> allergens);

    void setHomeAddress(Address address);

    User build(User u);
}
