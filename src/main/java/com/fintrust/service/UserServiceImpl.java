package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import com.fintrust.dao.UserDAO;
import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.User;
import com.fintrust.model.User.Status;
import com.fintrust.model.UserDetails;
import com.fintrust.util.NotificationUtil;

@Service
public class UserServiceImpl implements UserService {

	private Connection connection = DBConnection.getConnection();
	private UserDAO userDAO = new UserDAOImpl(connection);
	private UserDetailsDAO userDetailsDAO = new UserDetailsDAOImpl(connection);

	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

	@Autowired
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Autowired
	public void setUserDetailsDAO(UserDetailsDAO userDetailsDAO) {
		this.userDetailsDAO = userDetailsDAO;
	}

	@Override
	public User getUserByUserId(Long userId) throws SQLException {
		User user = userDAO.findById(userId);
		if (user != null) {
			return user;
		}
		return null;
	}

	@Override
	public boolean registerUser(User user) {
		// Check if email already exists
		try {
			if (userDAO.isEmailExists(user.getEmail())) {
				System.out.println("Email already registered.");
				return false;
			} else {
				System.out.println("Email not registered.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Encrypted the password
		String encrypted = encryptPassword(user.getPassword());
		user.setPassword(encrypted);

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
				e1.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Fetching logined user details
	 */
	public User getLoggedInUser() {
		Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
		try {
			return userDAO.findById(userId);
		} catch (SQLException e) {
			e.printStackTrace();
			return new User();
		}
	}
	
	/**
	 * Fetching logined user details
	 */
	public Long getNumberOfPendingKycRequest() {
		
		try {
			return userDAO.getNumberOfPendingKycRequest();
		} catch (SQLException e) {
			NotificationUtil.showInstant("error", "Failed to fetch user kyc request count");
			e.printStackTrace();
		}
		return 0l;
	}

	/**
	 * Fetching logned user password user details
	 */
	public String getLoggedInUserPassword() {
		Boolean isAdminRequested = (Boolean) Sessions.getCurrent().getAttribute("adminPasswordRequest");
	//	Executions.sendRedirect("/");
		Long userId = -1l;
		if (isAdminRequested == null || !isAdminRequested) {
			userId = (Long) Sessions.getCurrent().getAttribute("user_id");
		} else {
			userId = (Long) Sessions.getCurrent().getAttribute("admin_id");
		}

		try {
			return userDAO.findPasswordById(userId);

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	/**
	 * Updating user details
	 * 
	 * @param user
	 */
	public boolean updateUser(User user) {

		try {
			return userDAO.update(user);
		} catch (SQLException e) {
			logger.error("❌ Failed to update user Error", e);
			return false;
		}
	}

	/**
	 * Getting total registered users details
	 */
	public Long getTotalUsers() {
		try {
			return userDAO.getTotalUsers();
		} catch (SQLException e) {
			NotificationUtil.showInstant("error", "Failed to load user count." + e.getMessage());
			logger.error("❌ Failed to load user count Error:", e);
		}
		return 0l;
	}

	/**
	 * Future implementation
	 */
	@Override
	public void update2FA(UserDetails user) {

	}

	/**
	 * Authenticating the user
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	@Override
	public boolean isAuthorize(String userName, String password) {
		password = encryptPassword(password);
		// converting password into digest password
		try {
			User user = userDAO.authenticate(userName, password);
			if (user != null) {
				Sessions.getCurrent().setAttribute("user_email", user.getEmail());
				Sessions.getCurrent().setAttribute("user_name", user.getFullName());
				Sessions.getCurrent().setAttribute("user_id", user.getId());
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Authenticating the user
	 * 
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
		String encryptedPassword = encryptPassword(password);
		String email = null;
		Boolean isAdminRequested = (Boolean) Sessions.getCurrent().getAttribute("adminPasswordRequest");
		//	Executions.sendRedirect("/");
			
			if (isAdminRequested == null || !isAdminRequested) {
				email = (String) Sessions.getCurrent().getAttribute("user_email");
			} else {
				email = (String) Sessions.getCurrent().getAttribute("admin_email");
			}
		try {
		
			if (email == null)
				return false;
			return userDAO.updatePassword(email, encryptedPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String encryptPassword(String password) {
		// Encrypted the password
		String encryptedPassword = passwordEncoder.encode(password);
		return encryptedPassword;
	}

	@Override
	public boolean changeUserStatus(Long userId, Status updatedStatus) {
		try {
			if (userDAO.updateUserStatus(userId, updatedStatus)) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<User> getAllUser() throws SQLException {
		List<User> allUsers = userDAO.findAllUsers();
		if (allUsers == null) {
			NotificationUtil.showInstant("warning", "No user found in database !");
		}
		return allUsers;
	}
}
