package com.fintrust.service;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import com.fintrust.db.DBConnection;

public class CardServices {

  public boolean submitCardRequest(String accNumber,String cardTypes,String cardCat,String addresss,String remark) throws SQLException
  {
	  Long user_id = (Long) Sessions.getCurrent().getAttribute("user_id");
	  try {		  
			 String sql="insert into card_requests (account_number, card_type, card_category, user_id, delivery_address, remarks) values(?,?,?,?,?,?)";
			 
			 PreparedStatement pstm = DBConnection.getConnection().prepareStatement(sql);
			 pstm.setLong(1, Long.parseLong(accNumber));
			 pstm.setString(2, cardTypes);
			 pstm.setString(3, cardCat);
			 pstm.setLong(4, user_id);
			 pstm.setString(5, addresss);
			 pstm.setString(6, remark);
			 int n=pstm.executeUpdate();
			 return n > 0;
	} catch (Exception e) {
		Clients.showNotification(e.getMessage());		
	}
	  return false; 
	}

  public ResultSet getCardList()
  {
	  ResultSet rs=null;
	  long user_id = (Long) Sessions.getCurrent().getAttribute("user_id");            // select from session 
	  String sql="select * from issuedatmcard where user_id = ?";
	  try {
		PreparedStatement pstm=DBConnection.getConnection().prepareStatement(sql);
		pstm.setLong(1, user_id);
		 rs=pstm.executeQuery();
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block hhhh
		e.printStackTrace();
	}
	  return rs;	  
  }

  public boolean isCardAlreadyRequested(String accNumber) throws SQLException {
	  if (accNumber == null) return false;
	  long accountNumber = Long.parseLong(accNumber);
	    Connection con = DBConnection.getConnection();
	    PreparedStatement ps = con.prepareStatement(
	        "SELECT COUNT(*) FROM card_request WHERE account_no=? AND card_request_status IN ('PENDING', 'APPROVED', 'ISSUED')");
	    ps.setLong(1, accountNumber);
	    ResultSet rs = ps.executeQuery();
	    rs.next();
	    return rs.getInt(1) > 0;
	} 
 
}
