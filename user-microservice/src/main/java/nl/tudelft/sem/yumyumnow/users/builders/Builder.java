package nl.tudelft.sem.yumyumnow.users.builders;

import nl.tudelft.sem.yumyumnow.users.model.Address;
import nl.tudelft.sem.yumyumnow.users.model.User;

import java.util.List;

public interface Builder {
    Builder setName(String name);
    Builder setSurname(String surname);
    Builder setEmail(String email);
    Builder setBlockedStatus(boolean blocked);
    Builder setRole(User.RoleEnum role);
    Builder setAllergens(List<String> allergens);
    Builder setHomeAddress(Address address);
    User build();
}
