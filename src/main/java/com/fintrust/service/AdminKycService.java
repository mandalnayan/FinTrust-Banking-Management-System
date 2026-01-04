package com.fintrust.service;

import java.util.List;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.impl.AdminKycDAOImpl;
import com.fintrust.model.AdminKycDTO;
import com.fintrust.model.User;
import com.fintrust.model.UserKycDTO;
import com.fintrust.util.EncryptUtil;

public class AdminKycService {

	private final AdminKycDAOImpl dao = new AdminKycDAOImpl();
	private String secretKey = EncryptUtil.getSecretKey();

	public List<AdminKycDTO> fetchPendingKycs() {
		List<AdminKycDTO> requestList = dao.getPendingKycs();

//    	Decripting encrypted data
		requestList.forEach((ud) -> {
			try {
				if (ud.getMaskedAadhaar() != null) {
					String aadharUnmasked;
					aadharUnmasked = EncryptUtil.decrypt(ud.getMaskedAadhaar(), secretKey);

					ud.setMaskedAadhaar(aadharUnmasked);
				}
				if (ud.getMaskedPan() != null) {
					String panUnmasked = EncryptUtil.decrypt(ud.getMaskedPan(), secretKey);
					ud.setMaskedPan(panUnmasked);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return requestList;
	}

	public void approve(int userId) {
		Long adminId = (Long) Sessions.getCurrent().getAttribute("admin_id");
		dao.updateStatus(userId, User.KycStatus.UPDATED.name(), null, adminId);
	}

	public void reject(int userId, String remarks) {
		Long adminId = (Long) Sessions.getCurrent().getAttribute("admin_id");
		dao.updateStatus(userId, User.KycStatus.REJECTED.name(), remarks, adminId);
	}
}