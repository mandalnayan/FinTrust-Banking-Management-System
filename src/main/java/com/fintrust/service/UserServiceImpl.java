package com.fintrust.service;

import java.sql.SQLException;

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
    	
        try {
			if (userDAO.isEmailExists(user.getEmail())) {
			    System.out.println("Email already registered.");
			    return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

        // Encrypt password (optional, we will add later)
        // user.setPassword(PasswordUtil.encrypt(user.getPassword()));

        // Saving password digest instead of actual password
        String digestPassword = MessageDigestion.digestPassword(user.getPassword());
        user.setPassword(digestPassword);
        
        // Insert user to DB
        Long userId;
		try {
			userId = userDAO.create(user);
			return userId != -1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }
        
    public User getLoggedInUser() {
    		Long userId = (Long)Sessions.getCurrent().getAttribute("currentUserId");
    		try {
				return userDAO.findById(userId);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return new User();
			}
    }
    
    
    public boolean updateUser(User user) {
    		
    	    try {
				return userDAO.update(user);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
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
		String digestPassword = MessageDigestion.digestPassword(password);

		// converting password into digest password
		try {
			User user = userDAO.authenticate(userName, digestPassword);
			if (user != null) {
				Sessions.getCurrent().setAttribute("user_email", user.getEmail());
				Sessions.getCurrent().setAttribute("user_name", user.getFullName());
				Sessions.getCurrent().setAttribute("user_id", user.getId());
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Update password
	 * 
	 */
	@Override
	public boolean updatePassword(String password) {
		String digestedPassword = MessageDigestion.digestPassword(password);
		try {
			return userDAO.updatePassword(digestedPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
    
}
