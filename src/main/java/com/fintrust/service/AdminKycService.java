package com.fintrust.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.impl.AdminKycDAOImpl;
import com.fintrust.model.AdminKycDTO;
import com.fintrust.model.User;
import com.fintrust.util.EncryptUtil;

/**
 * Service class to handle administrative KYC operations.
 * <p>
 * This class interacts with {@link AdminKycDAOImpl} to fetch pending KYC requests,
 * approve or reject user KYCs, and handle decryption of sensitive information.
 * <p>
 * Logging is implemented to track critical actions and errors, suitable for banking systems.
 */
public class AdminKycService {

    private static final Logger logger = LogManager.getLogger(AdminKycService.class);

    /** DAO object for admin KYC database operations */
    private final AdminKycDAOImpl dao = new AdminKycDAOImpl();

    /** Secret key used for decrypting sensitive user data */
    private final String secretKey = EncryptUtil.getSecretKey();

    /**
     * Fetches all pending KYC requests from the database and decrypts sensitive fields.
     *
     * @return a {@link List} of {@link AdminKycDTO} containing pending KYC requests
     */
    public List<AdminKycDTO> fetchPendingKycs() {
        List<AdminKycDTO> requestList = dao.getPendingKycs();
        logger.info("Fetched {} pending KYC requests from the database.", requestList.size());

        // Decrypting encrypted data
        requestList.forEach((ud) -> {
            try {
                if (ud.getMaskedAadhaar() != null) {
                    String aadharUnmasked = EncryptUtil.decrypt(ud.getMaskedAadhaar(), secretKey);
                    ud.setMaskedAadhaar(aadharUnmasked);
                    logger.debug("Decrypted Aadhaar for userId: {}", ud.getUserId());
                }
                if (ud.getMaskedPan() != null) {
                    String panUnmasked = EncryptUtil.decrypt(ud.getMaskedPan(), secretKey);
                    ud.setMaskedPan(panUnmasked);
                    logger.debug("Decrypted PAN for userId: {}", ud.getUserId());
                }
            } catch (Exception e) {
                logger.error("Error decrypting sensitive data for userId: {}", ud.getUserId(), e);
            }
        });

        return requestList;
    }

    /**
     * Approves a user's KYC request.
     *
     * @param userId the ID of the user whose KYC is being approved
     */
    public void approve(int userId) {
        Long adminId = (Long) Sessions.getCurrent().getAttribute("admin_id");
        dao.updateStatus(userId, User.KycStatus.UPDATED.name(), null, adminId);
        logger.info("KYC approved for userId: {} by adminId: {}", userId, adminId);
    }

    /**
     * Rejects a user's KYC request with remarks.
     *
     * @param userId  the ID of the user whose KYC is being rejected
     * @param remarks the remarks explaining the rejection
     */
    public void reject(int userId, String remarks) {
        Long adminId = (Long) Sessions.getCurrent().getAttribute("admin_id");
        dao.updateStatus(userId, User.KycStatus.REJECTED.name(), remarks, adminId);
        logger.info("KYC rejected for userId: {} by adminId: {}. Remarks: {}", userId, adminId, remarks);
    }
}
