package com.fintrust.service;

import java.sql.SQLException;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.model.UserDetails;
import com.fintrust.util.NotificationUtil;

public class UserDetailsServiceImpl {
	UserDetailsDAO userDAOImpl = null;

	public UserDetailsServiceImpl() {
		userDAOImpl = new UserDetailsDAOImpl();
	}

	public void updatePrimaryAccount(long userId, long accountId) {

		try {
			userDAOImpl.updatePrimaryAccount(userId, accountId);
		} catch (SQLException e) {
			NotificationUtil.showInstant("error", "Falied to updated primary account. please try again");
			e.printStackTrace();
		}
	}

	/**
	 * Getting the logined user details
	 * @return
	 */
	public UserDetails getLogedInDetails() {
		try {
			Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
			if (userId != null) {
				UserDetails ud = userDAOImpl.findByUserId(userId);
				if (ud != null)
					return ud;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NotificationUtil.push("error", "Faild to load user details. Please refresh the page.");
		return new UserDetails();
	}
	
	/**
	 * Updating the user detials
	 * @return
	 */
	public boolean updateDetails(UserDetails user) {
		return false;
	}

	/**
	 * Get primary account
	 * 
	 * @param userId
	 */
	public Long getPrimaryAccount(long userId) {

		try {
			Long accountId = userDAOImpl.findPrimaryAccount(userId);
			if (accountId != -1) {
				return accountId;
			}
		} catch (SQLException e) {
			NotificationUtil.showInstant("error", "Falied to find primary account. please try again");
			e.printStackTrace();
		}
		NotificationUtil.push("warning", "You haven't created account yet. Please create account.");
		return -1l;
	}
}
