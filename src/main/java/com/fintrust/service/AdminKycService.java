package com.fintrust.service;

import java.util.List;

import com.fintrust.dao.impl.AdminKycDAO;
import com.fintrust.model.AdminKycDTO;
import com.fintrust.util.EncryptUtil;

public class AdminKycService {

	private final AdminKycDAO dao = new AdminKycDAO();
	private String secretKey = EncryptUtil.getSecretKey();

	public List<AdminKycDTO> fetchPendingKycs() {
		List<AdminKycDTO> requestList = dao.getPendingKycs();

//    	Decreption encrypted data
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

	public void approve(int kycId, String admin) {
		dao.updateStatus(kycId, "UPDATED", null, admin);
	}

	public void reject(int kycId, String remarks, String admin) {
		dao.updateStatus(kycId, "REJECTED", remarks, admin);
	}
}
