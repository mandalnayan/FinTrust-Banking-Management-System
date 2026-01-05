package com.fintrust.service;

import java.sql.SQLException;
import java.util.List;

import com.fintrust.model.User;
import com.fintrust.model.User.Status;
import com.fintrust.model.UserDetails;

public interface UserService {
/**
 * User Registration
 * @param user
 * @return
 */
    boolean registerUser(User user);
    
    /**
     * Fetch loggedIn user data
     * @return
     */
    User getLoggedInUser();
    
    /**
     * Fetch loggedIn user password
     * @return
     */
	String getLoggedInUserPassword();
	
	/**
	 * matching password
	 * @param encodedPassword
	 * @param value
	 * @return
	 */
	boolean isPasswordMatch(String encodedPassword, String value);
    /**
     * Updated user data
     * @param user
     * @return
     */
    boolean updateUser(User user);
    
    /**
     * Updated user password
     * @param user
     * @return
     */
    boolean updatePassword(String newPassword);

    /**
     * Update 2FA
     * @param user
     */
	void update2FA(UserDetails user);
	
	/**
	 * Authenticating the user
	 * @param userName
	 * @param password
	 * @return
	 */
	boolean isAuthorize(String userName, String password);
	
	/**
	 * Get user by user name (email)
	 * @param userName
	 * @return
	 */
	User getUserByUserName(String userName);
	
	/**
	 * Get user by user id
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	User getUserByUserId(Long userId) throws SQLException;
	
	/**
	 * Get total users
	 * @return
	 * @throws SQLException
	 */
	Long getTotalUsers();
	
	boolean changeUserStatus(Long userId , Status updatedStatus);
	
	/**
	 * find all user details
	 * @return
	 * @throws SQLException
	 */
	List<User> getAllUser() throws SQLException ;

	/**
	 * Find user kyc request count
	 * @return
	 */
	Long getNumberOfPendingKycRequest();

}
