package com.fintrust.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Messagebox;

import com.fintrust.db.DBConnection;
import com.fintrust.model.AccountCloseRequest;
import com.fintrust.model.User;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

/**
 * DAO class for handling account close requests.
 *
 */
public class AccountCloseRequestDao {

    /* -------------------- Logger -------------------- */
    private static final Logger logger = LoggerFactory.getLogger(AccountCloseRequestDao.class);

    /* -------------------- Constants -------------------- */
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECT";

    private static final String SUCCESS = "success";
    private static final String WARNING = "warning";


    /**
     * Ensures required database table exists.
     */
    public AccountCloseRequestDao() {
        createAccountCloserSchema();
    }

    /**
     * Creates account_closer_request table if not exists.
     */
    public static void createAccountCloserSchema() {

        String query = """
            CREATE TABLE IF NOT EXISTS account_closer_request (
                request_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                account_number BIGINT UNSIGNED NOT NULL,
                reason VARCHAR(100),
                status ENUM('PENDING','REJECT','APPROVED') DEFAULT 'PENDING',
                requested_by BIGINT UNSIGNED NOT NULL,
                review_by BIGINT UNSIGNED NOT NULL,
                requested_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                review_date DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                remarks VARCHAR(255),
                FOREIGN KEY (account_number) REFERENCES accounts(account_number),
                FOREIGN KEY (requested_by) REFERENCES users(user_id),
                FOREIGN KEY (review_by) REFERENCES users(user_id)
            );
        """;

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            st.executeUpdate(query);
            logger.info("account_closer_request table verified/created");

        } catch (SQLException e) {
            logger.error("Error creating account_closer_request table", e);
            Messagebox.show("Database error while creating schema");
        }
    }

    /**
     * Saves a new account close request.
     *
     * @param req AccountCloseRequest
     * @return true if saved successfully
     */
    public boolean saveRequest(AccountCloseRequest req) {

        String query = """
            INSERT INTO account_closer_request
            (account_number, reason, requested_by, review_by)
            VALUES (?, ?, ?, 1)
        """;

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(query)) {
            ps.setLong(1, req.getAccountNo());
            ps.setString(2, req.getReason());
            ps.setLong(3, req.getRequestedBy().getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error saving account close request", e);
            Messagebox.show("Database error while saving close request");
        }
        return false;
    }

    /**
     * Checks whether a pending close request already exists.
     *
     * @param accountNo account number
     * @return true if pending request exists
     */
    public Boolean isRequestExist(long accountNo) {
        String query = "SELECT 1 FROM account_closer_request WHERE account_number = ? AND status = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(query)) {
            ps.setLong(1, accountNo);
            ps.setString(2, STATUS_PENDING);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            logger.error("Error checking existing close request", e);
        }
        return false;
    }

    /**
     * Fetches all pending account close requests.
     *
     * @return list of pending requests
     */
    public List<AccountCloseRequest> getAllPendingRequest() {
        List<AccountCloseRequest> list = new ArrayList<>();
        String query ="SELECT * FROM account_closer_request WHERE status = ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(query)) {
            ps.setString(1, STATUS_PENDING);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AccountCloseRequest req = new AccountCloseRequest();
                req.setRequestId(rs.getLong("request_id"));
                req.setAccountNo(rs.getLong("account_number"));
                req.setReason(rs.getString("reason"));

                long requestedBy = rs.getLong("requested_by");
                User user = new UserServiceImpl().getUserByUserId(requestedBy);
                
                req.setRequestedBy(user);
                req.setRequestDate(rs.getTimestamp("requested_date").toLocalDateTime());

                list.add(req);
            }

        } catch (SQLException e) {
            logger.error("Error fetching pending close requests", e);
            Messagebox.show("Database error while loading close requests");
        }
        return list;
    }

    /**
     * Approves account close request and closes the account.
     *
     * @param requestId request id
     * @param employeeId reviewer id
     * @param remarks reviewer remarks
     * @return true if approved successfully
     */
    public boolean approveRequest(long requestId,long employeeId,String remarks) {
        logger.info("Approving account close requestId={}", requestId);

        String fetchSql = "SELECT account_number FROM account_closer_request WHERE request_id = ?";
        String closeAccountSql ="UPDATE accounts SET status='closed' WHERE account_number=?";
        String updateRequestSql = """
        						    UPDATE account_closer_request
        						    SET status=?, review_by=?, review_date=NOW(), remarks=?
        						    WHERE request_id=?
        						""";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            long accountNo;
            try (PreparedStatement ps = con.prepareStatement(fetchSql)) {
                ps.setLong(1, requestId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    NotificationUtil.showInstant(WARNING, "Request not found");
                    return false;
                }
                accountNo = rs.getLong("account_number");
            }

            try (PreparedStatement ps = con.prepareStatement(closeAccountSql)) {
                ps.setLong(1, accountNo);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(updateRequestSql)) {
                ps.setString(1, STATUS_APPROVED);
                ps.setLong(2, employeeId);
                ps.setString(3, remarks);
                ps.setLong(4, requestId);
                ps.executeUpdate();
            }

            con.commit();
            Messagebox.show("Request approved successfully!",SUCCESS, Messagebox.OK, Messagebox.INFORMATION);
            return true;

        } catch (SQLException e) {
            logger.error("Error approving close request", e);
        }
        return false;
    }

    /**
     * Rejects an account close request.
     *
     * @param requestId request id
     * @param employeeId reviewer id
     * @param remarks rejection remarks
     * @return true if rejected successfully
     */
    public boolean rejectRequest(long requestId,long employeeId,String remarks) {
        String query = """
            UPDATE account_closer_request
            SET status=?, review_by=?, review_date=NOW(), remarks=?
            WHERE request_id=?
        """;

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(query)) {
            ps.setString(1, STATUS_REJECTED);
            ps.setLong(2, employeeId);
            ps.setString(3, remarks);
            ps.setLong(4, requestId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error rejecting close request", e);
        }
        return false;
    }

    /**
     * Returns number of pending close requests.
     *
     * @return pending request count
     */
    public Long getNumberOfPendingRequest() {
        String sql = "SELECT COUNT(*) FROM account_closer_request WHERE status=?";
        
        try (PreparedStatement ps =DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, STATUS_PENDING);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);

        } catch (SQLException e) {
            logger.error("Error counting pending close requests", e);
        }
        return 0L;
    }
}
