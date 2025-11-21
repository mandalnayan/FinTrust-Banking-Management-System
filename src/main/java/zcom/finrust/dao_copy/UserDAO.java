package zcom.finrust.dao_copy;

import com.fintrust.model_copy.User;

public interface UserDAO {
    boolean saveUser(User user);
    boolean updateUser(User user);
    boolean isEmailExists(String email);
    boolean isAuthorize(String userName, String password);
    User getUserByEmail(String email);
    boolean updatePassword(String password);
}

