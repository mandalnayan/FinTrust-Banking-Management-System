package com.fintrust.service;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.UserDAO;
import com.fintrust.dao.UserDAOImpl;
import com.fintrust.model.Customer;
import com.fintrust.model.User;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO = new UserDAOImpl();

    @Override
    public boolean registerUser(Customer user) {
        // Check if email already exists
        if (userDAO.isEmailExists(user.getEmail())) {
            System.out.println("Email already registered.");
            return false;
        }

        // Encrypt password (optional, we will add later)
        // user.setPassword(PasswordUtil.encrypt(user.getPassword()));

        // Save user to DB
        return userDAO.saveUser(user);
    }
    
    public Customer getLoggedInUser() {
    		String email = (String)Sessions.getCurrent().getAttribute("currentUser");
    		return userDAO.getUserByEmail(email);
    }
    
    public boolean updateUser(Customer customer) {
    		
    	    return userDAO.updateUser(customer);
    }

	@Override
	public void update2FA(Customer customer) {
		// TODO Auto-generated method stub
		
	}
    
}
