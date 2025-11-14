package com.fintrust.cards.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fintrust.db.DBConnection;


public class BlockUnblockVerification  extends SelectorComposer<Window>{

	@Wire
	Textbox txtPassword,txtOTP;
	
	@Wire
	Button btnVerifyPassword,btnSendOTP ,btnVerifyOTP,btnConfirm,btnCancel;
	
	@Wire
	Label lblEmail,isVerifyLbl;
	
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		txtOTP.setDisabled(true);
		btnSendOTP.setDisabled(true);
		btnConfirm.setDisabled(true);
		btnVerifyOTP.setDisabled(true);
		
	}
	
	
	
	 public void statusChange()
	 {
		
		//Executions.sendRedirect("/Card/block_unblock_verification.zul");
		 String doStatus;
	
	    String currentStatus=(String) Sessions.getCurrent().getAttribute("blockOrUnblock");
	    if(currentStatus.equalsIgnoreCase("block card"))
	    {
	        doStatus="Blocked";  
	    }else {
	    	 doStatus="Active"; 
	    }
	   
	    String atmCardNumber=(String)Sessions.getCurrent().getAttribute("atmNumber");
		
		// long atmNumber = (Long) Long.parseLong(atmCardNumber);
	     String sql = "UPDATE card SET card_status = ? WHERE card_number = ?";

		//String atmCardNumber=(String)Sessions.getCurrent().getAttribute("atmNumber");
		Connection connection=DBConnection.getConnection();
		
		try(  PreparedStatement pstm=connection.prepareStatement(sql))
		{
			pstm.setString(1, doStatus);
			pstm.setString(2, atmCardNumber );
			int  n = pstm.executeUpdate();
			//Executions.sendRedirect("");
			
		}catch(Exception e1)
		{
			e1.getMessage();
		}
      
	 }
	
	
	@Listen("onClick=button")
	public void buttonListener(Event e)
	{
	   Button targetBtn= (Button) e.getTarget();
	   String btnValue=targetBtn.getLabel();
	   if(btnValue.equalsIgnoreCase("Cancel"))
	   {
		   Executions.sendRedirect("/Card/manageCard.zul");
	   }
	   if(btnValue.equalsIgnoreCase("Verify Password"))
	   {
		  String enteredPassword=txtPassword.getValue();
		  String userPassword="Vikas12345";                  ////get from session
		  if(enteredPassword.equalsIgnoreCase(userPassword))
		  {
			     txtOTP.setDisabled(false);
			  	 btnSendOTP.setDisabled(false);
			  	isVerifyLbl.setValue("OTP verified!!!!!");
			  	Messagebox.show("OTP Verified!!!");
				 
		  }
		  else {
			  Messagebox.show("Invalid PAssord!!!!");
		  }
	   }
	   
	   
	   if(btnValue.equalsIgnoreCase("Send OTP to Email"))
	   {
		   Messagebox.show("OTP has been send to your registeded Mail Address");
		   btnSendOTP.setDisabled(true);
		   btnVerifyOTP.setDisabled(false);
		   
	   }
	   if(btnValue.equalsIgnoreCase("Verify OTP"))
	   {
		 String currentOtp="707070";                              //get it from mail 
		 if(txtOTP.getValue().equalsIgnoreCase(currentOtp)) 
		 {     Messagebox.show("OTP verified!!!!!!!!!");
			 btnConfirm.setDisabled(false);
		 }
		 else {
			   Messagebox.show("Plese ! enter valid OTP.");
		 }
	   }
	   
	   if(btnValue.equalsIgnoreCase("Confirm"))
	   {
		   
		 statusChange(); 
		 Messagebox.show("Your card is "+btnValue);
		 Executions.sendRedirect("/Card/manageCard.zul");
	   }
	  
	}
	
	
}
