package com.fintrust.dao;

import java.sql.*;
import java.util.*;

import org.zkoss.zul.Messagebox;

import com.fintrust.db.DBConnection;
import com.fintrust.model.AccountUpdateRequest;


public class AccountUpdateRequestDao {
	
	
	public boolean createSchema() {
		String query = """
					CREATE TABLE IF NOT EXISTS account_update_request (
					request_id BIGINT PRIMARY KEY AUTO_INCREMENT,
					account_no BIGINT NOT NULL,
					new_account_type ENUM('SAVINGS', 'CURRENT', 'SALARY'),
					new_branch_name VARCHAR(100),
					new_mode_of_operation ENUM('SELF', 'JOINT'),
					status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
					requested_by BIGINT,  -- customer_id
					reviewed_by BIGINT,   -- employee_id (NULL until approved/rejected)
					request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
					review_date DATETIME,
					FOREIGN KEY (account_no) REFERENCES account(account_no)
				); 
				""";
		
		try {
			Statement statement = DBConnection.getConnection().createStatement();
			statement.executeUpdate(query);
			System.out.println("updation table created");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
	}

	public boolean save(AccountUpdateRequest req) {
	    String sql = "INSERT INTO account_update_request " +
	                 "(account_no, new_account_type, new_branch_name, new_mode_of_operation, requested_by) " +
	                 "VALUES (?, ?, ?, ?, ?)";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, req.getAccountNo());
	        ps.setString(2, req.getNewAccountType());
	        ps.setString(3, req.getNewBranchName());
	        ps.setString(4, req.getNewModeOfOperation());
	        ps.setLong(5, req.getRequestedBy());

	        if(ps.executeUpdate()>0) {
	        	return true;
	        }

	    } catch (SQLException e) {
	    	Messagebox.show("Error while saving account update request:\n" + e.getMessage());
	        e.printStackTrace(); 
	    }
	    return false;
	}

	public List<AccountUpdateRequest> getPendingRequests() {
	    List<AccountUpdateRequest> list = new ArrayList<>();
	    String sql = "SELECT * FROM account_update_request WHERE status = 'PENDING'";

	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            AccountUpdateRequest r = new AccountUpdateRequest();
	            r.setRequestId(rs.getLong("request_id"));
	            r.setAccountNo(rs.getLong("account_no"));
	            r.setNewAccountType(rs.getString("new_account_type"));
	            r.setNewBranchName(rs.getString("new_branch_name"));
	            r.setNewModeOfOperation(rs.getString("new_mode_of_operation"));
	            r.setStatus(rs.getString("status"));
	            r.setRequestedBy(rs.getLong("requested_by"));
	            list.add(r);
	        }

	    } catch (SQLException e) {
	    	Messagebox.show("Error while fetching pending account update requests:\n" + e.getMessage());
	        e.printStackTrace(); 
	    }
	    return list;
	}


	public void approveRequest(long reqId, long empId) {
	    String fetchSql = "SELECT * FROM account_update_request WHERE request_id = ?";
	    String updateAccSql = "UPDATE account SET account_type = ?, branch_name = ?, mode_of_operation = ? WHERE account_no = ?";
	    String updateReqSql = "UPDATE account_update_request SET status = 'APPROVED', reviewed_by = ?, review_date = NOW() WHERE request_id = ?";

	    try (Connection conn = DBConnection.getConnection()) {
	        conn.setAutoCommit(false); // start transaction

	        // Step 1: Get request details
	        try (PreparedStatement ps = conn.prepareStatement(fetchSql)) {
	            ps.setLong(1, reqId);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    long accNo = rs.getLong("account_no");
	                    String type = rs.getString("new_account_type");
	                    String branch = rs.getString("new_branch_name");
	                    String mode = rs.getString("new_mode_of_operation");

	                    // Step 2: Update account details
	                    try (PreparedStatement upd = conn.prepareStatement(updateAccSql)) {
	                        upd.setString(1, type);
	                        upd.setString(2, branch);
	                        upd.setString(3, mode);
	                        upd.setLong(4, accNo);
	                        upd.executeUpdate();
	                    }
	                } else {
	                    Messagebox.show("Request not found for ID: " + reqId, "Error", Messagebox.OK, Messagebox.EXCLAMATION);
	                    return;
	                }
	            }
	        }

	        // Step 3: Update request status
	        try (PreparedStatement updReq = conn.prepareStatement(updateReqSql)) {
	            updReq.setLong(1, empId);
	            updReq.setLong(2, reqId);
	            updReq.executeUpdate();
	        }

	        conn.commit(); // all good — commit transaction
	        Messagebox.show("Request approved successfully!", "Success", Messagebox.OK, Messagebox.INFORMATION);

	    } catch (SQLException e) {
	        e.printStackTrace();
	        Messagebox.show("Error while approving request:\n" + e.getMessage(),
	                        "Database Error", Messagebox.OK, Messagebox.ERROR);
	        try {
	            // attempt rollback
	            Connection conn = DBConnection.getConnection();
	            conn.rollback();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	}

	public void rejectRequest(long reqId, long empId) {
	    String sql = "UPDATE account_update_request SET status = 'REJECTED', reviewed_by = ?, review_date = NOW() " +
	                 "WHERE request_id = ?";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, empId);
	        ps.setLong(2, reqId);
	        int rows = ps.executeUpdate();

	        if (rows > 0) {
	            Messagebox.show("Request rejected successfully.");
	        } else {
	            Messagebox.show("No request found with ID: " + reqId);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        Messagebox.show("Database Error while rejecting request:\n" + e.getMessage());
	    }
	}

}
