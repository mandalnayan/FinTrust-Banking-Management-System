package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.UserDetails;
import com.fintrust.util.EncryptUtil;
import com.fintrust.util.KeyUtil;
import com.fintrust.util.NotificationUtil;

@Service
public class UserDetailsServiceImpl {
	 @Autowired
	    private UserDetailsDAO userDAOImpl;

	    @Value("${fintrust.secretKey}")
	    private String secretKey;	

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
			System.out.println("Sec Key: " + secretKey);
			Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");

			if (userId != null) {
				UserDetails ud = userDAOImpl.findByUserId(userId);
				if (ud.getAadhaarMasked() != null) {
					String aadharUnmasked = EncryptUtil.decrypt(ud.getAadhaarMasked(), secretKey);
					ud.setAadhaarMasked(aadharUnmasked);
				}
				if (ud.getPanMasked() != null) {
					String panUnmasked = EncryptUtil.decrypt(ud.getPanMasked(), secretKey);
					ud.setPanMasked(panUnmasked);
				}
				if (ud != null)
					return ud;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NotificationUtil.showInstant("error", "Faild to load user details. Please refresh the page.");
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
			if (userDAOImpl == null) return false;
			String aadharMasked = EncryptUtil.encrypt(ud.getAadhaarMasked(), secretKey);
			ud.setAadhaarMasked(aadharMasked);
			
			// Encryptng pan no
			String panMasked = EncryptUtil.encrypt(ud.getPanMasked(), secretKey);
			ud.setPanMasked(panMasked);
	
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
			if (userDAOImpl == null) return -1l;
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
