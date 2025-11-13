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
     * Update 2FA
     * @param user
     */
	void update2FA(User user);
    
}
