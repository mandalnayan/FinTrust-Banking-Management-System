package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.UserDAO;
import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.User;
import com.fintrust.model.UserDetails;

@Service
public class UserServiceImpl implements UserService {

	private Connection connection = DBConnection.getConnection();
	 private UserDAO userDAO;
	 private UserDetailsDAO userDetailsDAO;

	    @Autowired
	    public void setUserDAO(UserDAO userDAO) {
	        this.userDAO = userDAO;
	    }

	    @Autowired
	    public void setUserDetailsDAO(UserDetailsDAO userDetailsDAO) {
	        this.userDetailsDAO = userDetailsDAO;
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
			// Setting auto commit false either both will create or no one
			connection.setAutoCommit(false);
			userId = userDAO.create(user);
//			Inserting empty userDetails
			
			if (userId != -1 && userDetailsDAO.createEmptyUserDetails(userId) != -1) {
				connection.commit();
				return true;
			}
			return userId != -1;
		} catch (SQLException e) {
			e.printStackTrace();
			// Roll-back 
			try {
				connection.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return false;
    }
        
    /**
     * Fetching logined user details
     */
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

    /**
     * Future implementatin
     */
	@Override
	public void update2FA(UserDetails user) {
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
	 * Authenticating the user
	 * @param userName
	 * @param password
	 * @return
	 */
	@Override
	public User getUserByUserName(String userName) {		

		// converting password into digest password
		try {
			User user = userDAO.findByEmail(userName);
			return user;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
