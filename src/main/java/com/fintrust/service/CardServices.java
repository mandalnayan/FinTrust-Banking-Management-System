package com.fintrust.service;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.zkoss.zk.ui.util.Clients;

import com.fintrust.db.DBConnection;

public class CardServices {

  public void submitCardRequest(String accNumber,String cardTypes,String cardCat,String addresss,String remark) throws SQLException
  {
	  try {
		  System.out.println("9");
		 
			 String sql="insert into card_request (account_no,card_type,card_category,customer_id, address,remarks) values(?,?,?,1,?,?)";
			 System.out.println("2");
			 PreparedStatement pstm = DBConnection.getConnection().prepareStatement(sql);
			 pstm.setLong(1, Long.parseLong(accNumber));
			 pstm.setString(2, cardTypes);
			 pstm.setString(3, cardCat);
			 pstm.setString(4, addresss);
			 pstm.setString(5, remark);
			 int n=pstm.executeUpdate();
	} catch (Exception e) {
		Clients.showNotification(e.getMessage());
	}
	 
	
  }

  public ResultSet getCardList()
  {
	  ResultSet rs=null;
	  String customer_id="chgf";              // select from session 
	  String sql="select * from  issuedatmcard where customer_id=?";
	  try {
		PreparedStatement pstm=DBConnection.getConnection().prepareStatement(sql);
		pstm.setString(1, customer_id);
		 rs=pstm.executeQuery();
		
	} catch (SQLException e) {
		// TODO Auto-generated catch block hhhh
		e.printStackTrace();
	}
	  return rs;
	  
  }

  public boolean isCardAlreadyRequested(String accNumber) throws SQLException {
	    Connection con = DBConnection.getConnection();
	    PreparedStatement ps = con.prepareStatement(
	        "SELECT COUNT(*) FROM card_request WHERE account_no=? AND status IN ('PENDING','APPROVED')");
	    ps.setString(1, accNumber);
	    ResultSet rs = ps.executeQuery();
	    rs.next();
	    return rs.getInt(1) > 0;
	}

 
 
}
