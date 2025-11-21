package zcom.finrust.dao_copy;

import com.fintrust.model_copy.Customer;

public interface UserDAO {
    boolean saveUser(Customer user);
    boolean updateUser(Customer user);
    boolean isEmailExists(String email);
    boolean isAuthorize(String userName, String password);
    Customer getUserByEmail(String email);
    boolean updatePassword(String password);
}

