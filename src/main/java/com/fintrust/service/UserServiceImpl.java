package com.fintrust.service;

import com.fintrust.dao.UserDAO;
import com.fintrust.dao.UserDAOImpl;
import com.fintrust.model.User;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO = new UserDAOImpl();

    @Override
    public boolean registerUser(User user) {
        // Check if email already exists
        if (userDAO.isEmailExists(user.getEmail())) {
            System.out.println("Email already registered.");
            return false;
        }

        // Encrypt password (optional, you can add later)
        // user.setPassword(PasswordUtil.encrypt(user.getPassword()));

        // Save user to DB
        return userDAO.saveUser(user);
    }
}
