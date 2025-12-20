package com.fintrust.cards.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
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
	    System.out.println(currentStatus);
	    
	    if(currentStatus.equalsIgnoreCase("Block Card"))
	    {  
	        doStatus="blocked";  
	    }else { 
	    	 doStatus="active"; 
	    }
	 
	    String card_number_masked=(String)Sessions.getCurrent().getAttribute("card_number_masked");
	    
		// long atmNumber = (Long) Long.parseLong(atmCardNumber);
	     String sql = "UPDATE cards SET card_status = ? WHERE card_number_masked = ?";
	    
		//String atmCardNumber=(String)Sessions.getCurrent().getAttribute("card_number_masked");
		Connection connection=DBConnection.getConnection();
		  
		try(  PreparedStatement pstm=connection.prepareStatement(sql))
		{ 
			pstm.setString(1, doStatus);
			pstm.setString(2, card_number_masked );
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
		  String userPassword="Vikas12345";                  ////get from session or encrpted and get from DB and compare 
		  
		  if(enteredPassword.equalsIgnoreCase(userPassword))
		  {
			     txtOTP.setDisabled(false);
			  	 btnSendOTP.setDisabled(false);
			  	isVerifyLbl.setValue("OTP verified!!!!!");
			  
			  	Clients.showNotification(
					    "Password Verified!!!",
					    Clients.NOTIFICATION_TYPE_INFO,
					    null,
					    "top_center",
					    3000
					);
				 
		  }
		  else {
				Clients.showNotification(
					    "Invalid Password!!!!",
					    Clients.NOTIFICATION_TYPE_ERROR,
					    null,
					    "top_center",
					    3000
					);
			 
		  }
	   }
	   
	   
	   if(btnValue.equalsIgnoreCase("Send OTP to Email"))
	   {
			Clients.showNotification(
				    "OTP has been send to your registeded Mail Address",
				    Clients.NOTIFICATION_TYPE_INFO,
				    null,
				    "top_center",
				    3000
				);
		
		   btnSendOTP.setDisabled(true);
		   btnVerifyOTP.setDisabled(false);
		   
	   }
	   if(btnValue.equalsIgnoreCase("Verify OTP"))
	   {
		 String currentOtp="707070";                              //get it from mail 
		 if(txtOTP.getValue().equalsIgnoreCase(currentOtp)) 
		 {   
			 Clients.showNotification(
					    "OTP verified!!!!!!!!!",
					    Clients.NOTIFICATION_TYPE_INFO,
					    null,
					    "top_center",
					    3000
					);
			
			 btnConfirm.setDisabled(false);
		 }
		 else {
			 Clients.showNotification(
					    "OTP verification Failed!!!!",
					    Clients.NOTIFICATION_TYPE_ERROR,
					    null,
					    "top_center",
					    3000
					);
		 }
	   }
	   
	   if(btnValue.equalsIgnoreCase("Confirm"))
	   {
		   
		 statusChange(); 
		 Clients.showNotification(
				 "Your card is "+(String) Sessions.getCurrent().getAttribute("blockOrUnblock"),
				    Clients.NOTIFICATION_TYPE_ERROR,
				    null,
				    "top_center",
				    3000
				);
		 // Executions.sendRedirect("/Card/manageCard.zul");
		
		 String contextPath = Executions.getCurrent().getContextPath();

		 Clients.evalJavaScript(
		     "setTimeout(function(){ window.location.href='" 
		     + contextPath + "/Card/manageCard.zul'; }, 3000);"
		 );


	   }
	  
	}
	
	
}