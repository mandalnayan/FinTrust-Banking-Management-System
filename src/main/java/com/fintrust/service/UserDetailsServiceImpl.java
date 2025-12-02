package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.UserDetails;
import com.fintrust.util.EncryptUtil;
import com.fintrust.util.KeyUtil;
import com.fintrust.util.NotificationUtil;

public class UserDetailsServiceImpl {
	private UserDetailsDAO userDAOImpl = null;
	private Connection connection = DBConnection.getConnection();
	private String secretKey;
	
	public UserDetailsServiceImpl() {
		userDAOImpl = new UserDetailsDAOImpl(connection);
		secretKey = KeyUtil.getKey();
	}

	public void updatePrimaryAccount(long userId, long accountId) {

		try {
			if (userDAOImpl.updatePrimaryAccount(userId, accountId))
				NotificationUtil.showInstant("info", "Updated primary account");
			return;
		} catch (SQLException e) {

			e.printStackTrace();
		}
		NotificationUtil.showInstant("error", "Failed to update. \nPlease try again!");
	}

	/**
	 * Getting the logined user details
	 * 
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
	 * Updating the user profile details
	 * 
	 * @return
	 */
	public boolean updateProfile(UserDetails user) {
		try {
			
			return userDAOImpl.updateProfile(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Updating the user profile details
	 * 
	 * @return
	 */
	public boolean updateKyc(UserDetails ud) {
		try {
			// Encryptng aadhar no
			String aadharMasked = EncryptUtil.encrypt(ud.getAadhaarMasked(), secretKey);
			ud.setAadhaarMasked(aadharMasked);
			
			// Encryptng pan no
			String panMasked = EncryptUtil.encrypt(ud.getPanMasked(), secretKey);
			ud.setAadhaarMasked(panMasked);
			
			return userDAOImpl.updateKyc(ud);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
