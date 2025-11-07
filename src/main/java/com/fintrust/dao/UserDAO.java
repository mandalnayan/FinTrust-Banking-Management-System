package com.fintrust.dao;

import com.fintrust.model.User;

public interface UserDAO {
    boolean saveUser(User user);
    boolean isEmailExists(String email);
    boolean isAuthorize(String userName, String password);
}

