package com.fintrust.service;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.UserDAO;
import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.model.User;


public class UserServiceImpl implements UserService {

    private UserDAO userDAO = null;
    
    public UserServiceImpl() {
    	userDAO = new UserDAOImpl();
    }

    @Override
    public boolean registerUser(User user) {
        // Check if email already exists
        if (userDAO.isEmailExists(user.getEmail())) {
            System.out.println("Email already registered.");
            return false;
        }

        // Encrypt password (optional, we will add later)
        // user.setPassword(PasswordUtil.encrypt(user.getPassword()));

        // Saving password digest instead of actual password
        String digestPassword = PasswordDigestion.digestPassword(user.getPassword());
        user.setPassword(digestPassword);
        
        // Insert user to DB
        Long userId = userDAO.create(user);
        return userId != -1;
    }
    
    public User getLoggedInUser() {
    		String email = (String)Sessions.getCurrent().getAttribute("currentUser");
    		return userDAO.getUserByEmail(email);
    }
    
    public boolean updateUser(User user) {
    		
    	    return userDAO.update(user);
    }

	@Override
	public void update2FA(User user) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Authenticating the user
	 * @param userName
	 * @param password
	 * @return
	 */
	@Override
	public boolean isAuthorize(String userName, String password) {
		 String digestPassword = PasswordDigestion.digestPassword(password);
		 
		// converting password into digest password 
	   return  userDAO.isAuthorize(userName, digestPassword);
	}
    
}
