package com.fintrust.service;

import com.fintrust.model.User;

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
	void update2FA(User user);
	
	/**
	 * Authenticating the user
	 * @param userName
	 * @param password
	 * @return
	 */
	boolean isAuthorize(String userName, String password);
    
}
