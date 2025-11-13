package com.fintrust.service;

import org.zkoss.zk.ui.Sessions;

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
    
    public User getLoggedInUser() {
    	String email = (String)Sessions.getCurrent().getAttribute("currentUser");
    	System.out.println("Lognied User: " + email);
    	return userDAO.getUserByEmail(email);
    }
    
    public boolean updateUser(User user) {
    	return false;
    }

	@Override
	public void update2FA(User user) {
		// TODO Auto-generated method stub
		
	}
    
}
