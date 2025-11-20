package com.fintrust.service;

import com.fintrust.model.Customer;

public interface UserService {
/**
 * User Registration
 * @param user
 * @return
 */
    boolean registerUser(Customer user);
    
    /**
     * Fetch loggedIn user data
     * @return
     */
    Customer getLoggedInUser();
    
    /**
     * Updated user data
     * @param user
     * @return
     */
    boolean updateUser(Customer user);

    /**
     * Update 2FA
     * @param user
     */
	void update2FA(Customer user);
	
	/**
	 * Authenticating the user
	 * @param userName
	 * @param password
	 * @return
	 */
	boolean isAuthorize(String userName, String password);
    
}
