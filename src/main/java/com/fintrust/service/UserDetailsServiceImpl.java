package com.fintrust.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.User;
import com.fintrust.model.UserDetails;
import com.fintrust.model.UserDocument;
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

//	    Media file
	private UserDocument addressDoc, photoDoc;

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
			e.printStackTrace();
		}
		NotificationUtil.showInstant("error", "Faild to load user details. Please refresh the page.");
		return new UserDetails();
	}

	/**
	 * Getting the logined user details
	 * 
	 * @return
	 */
	public User getLogedInUser() {
		try {
			Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");

			if (userId != null) {
				User ud = userDAOImpl.findById(userId);

				if (ud != null)
					return ud;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		NotificationUtil.showInstant("error", "Faild to load user. Please refresh the page.");
		return new User();
	}

	/**
	 * Getting the logined admin details
	 * 
	 * @return
	 */
	public UserDetails getLogedInAdminDetails() {
		try {
			Long userId = (Long) Sessions.getCurrent().getAttribute("admin_id");

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

				return ud;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
		
//		Base case
		if (userDetailsDAOImpl == null)
			return false;
		try {
			// loading document
			Session session = Sessions.getCurrent();
			addressDoc = (UserDocument) session.getAttribute("addressDoc");
			photoDoc = (UserDocument) session.getAttribute("photoDoc");
			// updating address
			if (addressDoc.getMedia() != null) {
			String addFileName = UUID.randomUUID() + "_" + ud.getUserId()  + "$" + addressDoc.getOriginalFileName();
			ud.setAddressProofFileName(addFileName);
			addressDoc.setStoredFileName(addFileName);
			}
			
			// Update photo
			if (photoDoc.getMedia() != null) {
				String photoFileName = UUID.randomUUID() + "_" + ud.getUserId() +  "$"  + photoDoc.getOriginalFileName();
				ud.setPhotoFileName(photoFileName);
				photoDoc.setStoredFileName(photoFileName);
			}
			
			// Encrypting Aadhar no
			
			String aadharMasked = EncryptUtil.encrypt(ud.getAadhaarMasked(), secretKey);
			ud.setAadhaarMasked(aadharMasked);

			// Encrypting pan no
			String panMasked = EncryptUtil.encrypt(ud.getPanMasked(), secretKey);
			ud.setPanMasked(panMasked);

//			Make connection auto commit false. To make sure either both will update or none
			connection.setAutoCommit(false);

			// Update user details
			if (userDAOImpl.update(ud.getUser()) && userDetailsDAOImpl.updateKyc(ud)) {
				// Saving document
				saveDocument(addressDoc);
				saveDocument(photoDoc);
				
				connection.commit();
				return true;
			}
		} catch (Exception e) {
//			Do rollback
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			NotificationUtil.showInstant("error", e.getMessage());
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
			if (userDetailsDAOImpl == null)
				return -1l;
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

	public UserDetails getUserDetails(Long userId) throws SQLException {
		UserDetails userDetail = userDetailsDAOImpl.findByUserId(userId);
		if (userDetail != null) {
			return userDetail;
		}
		return null;
	}

	// Save document
	private void saveDocument(UserDocument doc) throws IOException {
		Media media = doc.getMedia();
		String filePath = doc.getStoragePath();

		// if media is null
		if (media == null || filePath == null || filePath.isBlank())
			return;
		// File name
		String fileName = doc.getStoredFileName();

		String realPath = Executions.getCurrent().getDesktop().getWebApp().getRealPath(filePath);

		File dir = new File(realPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// Target file
		File file = new File(realPath + File.separator + fileName);

		// Save file
		if (media.isBinary()) {
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(media.getByteData());
			}
		} else {
			// For text-based media
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
				writer.write(media.getStringData());
			}
		}

	}

}
