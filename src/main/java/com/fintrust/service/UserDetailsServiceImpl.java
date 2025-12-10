package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.UserDetails;
import com.fintrust.util.EncryptUtil;
import com.fintrust.util.KeyUtil;
import com.fintrust.util.NotificationUtil;

@Service
public class UserDetailsServiceImpl {
	 @Autowired
	    private UserDetailsDAO userDetailsDAOImpl;
	 	private UserDAOImpl userDAOImpl;
	 	
	 	private Connection connection = DBConnection.getConnection();
	 	private final String secretKey = "fgso98/uasjX4kblCr/YSD0UW31DOmAslKZnvC6Rxfg=";
	   	
	 	public UserDetailsServiceImpl() {
	 		userDetailsDAOImpl = new UserDetailsDAOImpl(connection);	
	 		userDAOImpl = new UserDAOImpl(connection);
	 	}

	public void updatePrimaryAccount(long accountId) {

		try {
			Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
			if (userId != null) {
			if (userDetailsDAOImpl.updatePrimaryAccount(userId, accountId))
				NotificationUtil.showInstant("info", "Updated primary account");
				return;
			}
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
				UserDetails ud = userDetailsDAOImpl.findByUserId(userId);
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
			return userDetailsDAOImpl.updateProfile(user);
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
			if (userDetailsDAOImpl == null) return false;
			String aadharMasked = EncryptUtil.encrypt(ud.getAadhaarMasked(), secretKey);
			ud.setAadhaarMasked(aadharMasked);
			
			// Encrypting pan no
			String panMasked = EncryptUtil.encrypt(ud.getPanMasked(), secretKey);
			ud.setPanMasked(panMasked);
			
//			Make connection auto commit false. To make sure either both will update or nore
//			Update user details 
			if (userDAOImpl.update(ud.getUser()) && userDetailsDAOImpl.updateKyc(ud)) {
				connection.commit();
			}
		} catch (Exception e) {
//			Do rollback
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
			if (userDetailsDAOImpl == null) return -1l;
			Long accountId = userDetailsDAOImpl.findPrimaryAccount(userId);
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
