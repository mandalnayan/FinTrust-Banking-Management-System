package com.fintrust.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Messagebox;

import com.fintrust.db.DBConnection;
import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.model.Branch;
import com.fintrust.model.User;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

/**
 * DAO class for handling account update requests.
 */
public class AccountUpdateRequestDao {

    /* -------------------- Logger -------------------- */
    private static final Logger logger = LoggerFactory.getLogger(AccountUpdateRequestDao.class);

    /* -------------------- Constants -------------------- */
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";


    /**
     * Initializes table schema if not present.
     */
    public AccountUpdateRequestDao() {
        createSchema();
    }

    /**
     * Creates database schema for account update requests.
     *
     * @return true if schema created or exists, false otherwise
     */
    public boolean createSchema() {

        String query = """
            CREATE TABLE IF NOT EXISTS account_update_request (
                request_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
                account_number BIGINT UNSIGNED NOT NULL,
                new_account_type ENUM('SAVINGS', 'CURRENT', 'SALARY'),
                new_branch_id BIGINT,
                new_mode_of_operation ENUM('SELF', 'JOINT') DEFAULT 'SELF',
                status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
                requested_by BIGINT UNSIGNED NOT NULL,
                reviewed_by BIGINT UNSIGNED,
                request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                review_date DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (account_number) REFERENCES accounts(account_number),
                FOREIGN KEY (requested_by) REFERENCES users(user_id),
                FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
            );
        """;

        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            stmt.executeUpdate(query);
            logger.info("Account update request table verified/created");
            return true;
        } catch (SQLException e) {
            logger.error("Failed to create account_update_request table", e);
        }
        return false;
    }

    /**
     * Saves a new account update request.
     *
     * @param req AccountUpdateRequest object
     * @return true if saved successfully
     */
    public boolean save(AccountUpdateRequest req) {

        String sql = """
            INSERT INTO account_update_request
            (account_number, new_account_type, new_branch_id,
             new_mode_of_operation, requested_by)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, req.getAccountNo());
            ps.setString(2, req.getNewAccountType());
            ps.setLong(3, req.getBranchId());
            ps.setString(4, req.getNewModeOfOperation());
            ps.setLong(5, req.getRequestedBy().getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error saving account update request", e);
        }
        return false;
    }

    /**
     * Checks if a pending update request exists for an account.
     *
     * @param accountNumber account number
     * @return true if pending request exists
     */
    public Boolean isRequestExists(Long accountNumber) {
        String sql =
                "SELECT 1 FROM account_update_request " +
                "WHERE account_number = ? AND status = ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            ps.setString(2, STATUS_PENDING);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            logger.error("Error checking existing request", e);
            Messagebox.show("Error while checking request status");
        }
        return null;
    }

    /**
     * Fetches all pending account update requests.
     *
     * @return list of pending requests
     */
    public List<AccountUpdateRequest> getPendingRequests() {
        List<AccountUpdateRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM account_update_request WHERE status = ?";

        try (PreparedStatement ps =DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, STATUS_PENDING);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AccountUpdateRequest r = new AccountUpdateRequest();
                r.setRequestId(rs.getLong("request_id"));
                r.setAccountNo(rs.getLong("account_number"));
                r.setNewAccountType(rs.getString("new_account_type"));
                r.setBranchId(rs.getLong("new_branch_id"));
                r.setNewModeOfOperation(rs.getString("new_mode_of_operation"));
                r.setStatus(rs.getString("status"));

                long requestedBy = rs.getLong("requested_by");
                User user = new UserServiceImpl().getUserByUserId(requestedBy);
                
                r.setRequestedBy(user);
                r.setRequestDate(rs.getTimestamp("request_date").toLocalDateTime());

                Branch branch =new BranchDao().findById(r.getBranchId());
                r.setNewBranchName(branch.getBranchName());
                
                list.add(r);
            }

        } catch (SQLException e) {
            logger.error("Error fetching pending update requests", e);
        }
        return list;
    }

    /**
     * Approves an account update request and updates account details.
     *
     * @param reqId request id
     * @param empId reviewer employee id
     */
    public boolean approveRequest(long reqId, long empId) {
        logger.info("Approving account update requestId={}", reqId);

        String fetchSql = "SELECT * FROM account_update_request WHERE request_id = ?";
        String updateAccountSql = "UPDATE accounts SET account_type=?, branch_id=? , account_ownership_type=? WHERE account_number=?";
        String updateRequestSql ="UPDATE account_update_request SET status=?, reviewed_by=?, review_date=NOW() WHERE request_id=?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(fetchSql)) {
                ps.setLong(1, reqId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) return false;

                try (PreparedStatement upd = conn.prepareStatement(updateAccountSql)) {
                    upd.setString(1, rs.getString("new_account_type"));
                    upd.setLong(2, rs.getLong("new_branch_id"));
                    upd.setString(3, rs.getString("new_mode_of_operation"));
                    upd.setLong(4, rs.getLong("account_number"));
                    upd.executeUpdate();
                }
            }

            try (PreparedStatement updReq =conn.prepareStatement(updateRequestSql)) {
                updReq.setString(1, STATUS_APPROVED);
                updReq.setLong(2, empId);
                updReq.setLong(3, reqId);
                updReq.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            logger.error("Error approving update request", e);
            NotificationUtil.showInstant("error", e.getMessage());
        }
        return false;
    }

    /**
     * Rejects an account update request.
     *
     * @param reqId request id
     * @param empId reviewer id
     */
    public void rejectRequest(long reqId, long empId) {
        logger.info("Rejecting account update requestId={}", reqId);

        String sql = "UPDATE account_update_request SET status=?, reviewed_by=? , review_date=NOW() WHERE request_id=?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, STATUS_REJECTED);
            ps.setLong(2, empId);
            ps.setLong(3, reqId);
            ps.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error rejecting update request", e);
        }
    }

    /**
     * Returns count of pending update requests.
     *
     * @return number of pending requests
     */
    public Long getNumberOfPendingRequest() {
        String sql = "SELECT COUNT(*) FROM account_update_request WHERE status=?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, STATUS_PENDING);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);

        } catch (SQLException e) {
            logger.error("Error counting pending requests", e);
        }
        return 0L;
    }
}
