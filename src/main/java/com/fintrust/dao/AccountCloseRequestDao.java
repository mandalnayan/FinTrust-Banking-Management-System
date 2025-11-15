package com.fintrust.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.Messagebox;

import com.fintrust.db.DBConnection;
import com.fintrust.model.AccountCloseRequest;


public class AccountCloseRequestDao {

	
	public AccountCloseRequestDao() {
		createAccountCloserSchema();
	}
	
	public static void createAccountCloserSchema() {
		String query = """
					create table IF NOT EXISTS account_closer_request (
				request_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                account_no BIGINT NOT NULL,
                reason varchar(100),
                status ENUM('PENDING','REJECT','APPROVED') DEFAULT 'PENDING',
                requested_by BIGINT,
                review_by BIGINT,
				requested_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                review_date TIMESTAMP,
                remarks VARCHAR(255),              -- employee remarks
                FOREIGN KEY (account_no) REFERENCES account(account_no)
                );
				""";
		try(Connection con = DBConnection.getConnection()){
			if(con != null) {
				Statement st = con.createStatement();
				if(st.executeUpdate(query) > 0) {
					System.out.println("Account closer Request schme successful created");
					Messagebox.show("Account closer Request schme successful created");
				}
			}
			else {
				System.out.println("db connection is not created successful");
				Messagebox.show("db connection is not created successful");
			}
		} catch (SQLException e) {
			Messagebox.show("Database Error in CreateAccountCloserSchema "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public boolean saveRequest(AccountCloseRequest req) {
		String query =  """
						INSERT INTO account_closer_request 
						(account_no,reason, requested_by)
						values (?,?,?);
				""" ; 
		
		try(Connection con = DBConnection.getConnection()){
			if(con != null) {
				PreparedStatement statement = con.prepareStatement(query);
				statement.setLong(1, req.getAccountNo());
				statement.setString(2, req.getReason());
				statement.setLong(3, req.getRequestedBy());
				
				if(statement.executeUpdate() > 0) {
					//System.out.println("Account closer Request successfully save in db");
					//Messagebox.show("Account closer Request successfully save in db");
					return true;
				}
			}
		} catch (SQLException e) {
			Messagebox.show("Databaes error in save close request dao method" + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	public List<AccountCloseRequest> getAllPendingRequest(){
		String query = "select * from account_closer_request where status = 'PENDING';";
		List<AccountCloseRequest> list = new ArrayList<>();
		try(Connection con = DBConnection.getConnection()){
			if(con != null) {
				PreparedStatement statement = con.prepareStatement(query);
				ResultSet rs = statement.executeQuery();
				
				while(rs.next()) {
					AccountCloseRequest req =new AccountCloseRequest();
					req.setRequestId(rs.getLong("request_id"));
					req.setAccountNo(rs.getLong("account_no"));
					req.setReason(rs.getString("reason"));
					list.add(req);
				}
			}
		} catch (SQLException e) {
			Messagebox.show("Databaes error in getAllPenging close request dao method" + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	public boolean approveRequest(long requestId , long employeeId , String remarks) {
		String fetchDetailsAboutRequest = "select * from account_closer_request where request_id = ? ;";
		String closeAccountQuery = "UPDATE account SET account_status='CLOSED' WHERE account_no=?";
		
		String UpdateStatusQuery = """
											update account_closer_request 
											set status='APPROVED' , review_by = ? , review_date = NOW() , remarks = ?
											where request_id = ?
									""";
		PreparedStatement ps;
		//step 1 :- find the account no by request id
		try(Connection con = DBConnection.getConnection()){
			con.setAutoCommit(false); // start transaction
			
			ps = con.prepareStatement(fetchDetailsAboutRequest);
			ps.setLong(1, requestId);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				long accountNo = rs.getLong("account_no");
				//step 2 :- delete account from account table using account no	
				ps = con.prepareStatement(closeAccountQuery);
				ps.setLong(1, accountNo);
				ps.executeUpdate();
			}
			else {
				Messagebox.show("No request found with ID: " + requestId, "Success", Messagebox.OK, Messagebox.INFORMATION);
			}
			
			//step-3 update the closer request table 
			ps = con.prepareStatement(UpdateStatusQuery);
			ps.setLong(1, employeeId);
			ps.setString(2, remarks);
			ps.setLong(3, requestId);
		
			if(ps.executeUpdate()>0) {
				con.commit(); // all good — commit transaction
				 Messagebox.show("Request approved successfully!", "Success", Messagebox.OK, Messagebox.INFORMATION);
				 return true;
			}	
			
		} catch (SQLException e) {
			Messagebox.show("Error while approving request:\n" + e.getMessage(),
                    "Database Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
			
            try {
            	Connection conn = DBConnection.getConnection();
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}
	
	
	public boolean rejectRequest(long requestId , long employeeId , String remarks) {
		String UpdateStatusQuery = """
										update account_closer_request 
										set status='REJECT' , review_by = ? , review_date = NOW() , remarks = ?
										where request_id = ?
									""";
		PreparedStatement ps;
		//step 1 :- find the account no by request id
		try(Connection con = DBConnection.getConnection()){
			ps = con.prepareStatement(UpdateStatusQuery);
			ps.setLong(1, employeeId);
			ps.setString(2, remarks);
			ps.setLong(3, requestId);
		
			if(ps.executeUpdate()>0) {
				 Messagebox.show("Request rejected!", "Success", Messagebox.OK, Messagebox.INFORMATION);
				 return true;
			}	
			else {
	            Messagebox.show("No request found with ID: " + requestId, "Success", Messagebox.OK, Messagebox.INFORMATION);
	        }
			
		} catch (SQLException e) {
			Messagebox.show("Error while rejecting request:\n" + e.getMessage(),
                    "Database Error", Messagebox.OK, Messagebox.ERROR);
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	
	
	
	
}
