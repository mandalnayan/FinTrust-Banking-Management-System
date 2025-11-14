package com.fintrust.cards.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class cardStatusController  extends SelectorComposer<Window>{

	@Wire
	Label lblCardNo,lblType,lblStatus,lblMaxLimit;
	
	public void doAfterCompose(Window comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		alert("sdfsdfs");
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/fintrustbank","root","Vikas123");
		String atmCardNumber=(String)Sessions.getCurrent().getAttribute("atmNumber");
		long atmNumber = (Long) Long.parseLong(atmCardNumber);
		String sql="select * from card_request where atm_card_number =?" ;         
		PreparedStatement ptsm=connection.prepareStatement(sql);
		ptsm.setLong(1, atmNumber);
		
		
		
		ResultSet rs=ptsm.executeQuery();
		while(rs.next())
		{
			
		   lblCardNo.setValue(atmCardNumber);
		   lblType.setValue("Debit Card");
		   lblMaxLimit.setValue("50000");         //Add a column in Table for this max value 
		   lblStatus.setValue(rs.getString("card_status"));
			
		}
		
		
	}
	
}
