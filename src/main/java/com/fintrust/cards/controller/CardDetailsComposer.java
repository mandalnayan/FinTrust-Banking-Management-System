package com.fintrust.cards.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.fintrust.db.DBConnection;
import com.mysql.cj.xdevapi.Client;

public class CardDetailsComposer extends SelectorComposer<Window> {

	@Wire
	Label lblCardNo, lblType, lblMaxLimit, lblStatus, lblCurLimit, lblDailyLimit;

	@Wire
	Button btnBlock, btnUnblock;
	
	@Wire
	Row dailyLimitRow,sliderRow;

	long atmNumber;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);

		String card_number_masked = (String) Sessions.getCurrent().getAttribute("card_number_masked");

		String formattedCard = card_number_masked.replaceAll(
		        "(\\d{4})(\\d{4})(\\d{4})(\\d{4})",
		        "$1-$2-$3-$4"
		);
       System.out.println(formattedCard);
		Connection connection = DBConnection.getConnection();
		String sql = "select * from cards where card_number_masked=?"; 
		PreparedStatement ptsm = connection.prepareStatement(sql);
		ptsm.setString(1, card_number_masked);
        
		ResultSet rs = ptsm.executeQuery();
		while (rs.next()) {
            
			lblCardNo.setValue(formattedCard);
			lblType.setValue(rs.getString("card_type"));
			lblMaxLimit.setValue(rs.getString("maximum_limit"));
			lblStatus.setValue(rs.getString("card_status"));
			lblCurLimit.setValue(rs.getString("current_limit"));
			if (rs.getString("card_status").equalsIgnoreCase("Blocked")) {
				btnBlock.setVisible(false);
				lblStatus.setStyle("color: red; font-weight: bold;");
				sliderRow.setVisible(false);
				
				// btnUnblock.setVisible(false);
				// btnBlock.setDisabled(true);
			} else {
				if (rs.getString("card_status").equalsIgnoreCase("Expired")) {
					dailyLimitRow.setVisible(false);
					sliderRow.setVisible(false);
					lblStatus.setStyle("color: red; font-weight: bold;");

					//sliderRow.setValue("Unable to manage This C");
					btnBlock.setVisible(false);
					btnUnblock.setVisible(false);
				} else {
					if (rs.getString("card_status").equalsIgnoreCase("Active")) {
						btnUnblock.setVisible(false);

					}
				}
			}

		}
		
		

	}

	@Listen("onClick=#btnBlock,#btnUnblock")
	public void statusChange(Event e) {
		Button b = (Button) e.getTarget();
		String blkOrUblk = b.getLabel();
		Sessions.getCurrent().setAttribute("blockOrUnblock", blkOrUblk);
		Executions.sendRedirect("/user/card/block_unblock_verification.zul");
//		 String doStatus;
//		Button b=(Button) e.getTarget();
//	    String s=b.getLabel();
//	    if(s.equalsIgnoreCase("block card"))
//	    {
//	        doStatus="Blocked";  
//	    }else {
//	    	 doStatus="Active"; 
//	    }
//	    
//	    
//	    
//	     String sql = "UPDATE issuedatmcard_dummy SET card_status = ? WHERE atm_card_number = ?";
//;
//		//String atmCardNumber=(String)Sessions.getCurrent().getAttribute("atmNumber");
//		Connection connection=DbConnection.getConnection();
//		
//		try(  PreparedStatement pstm=connection.prepareStatement(sql))
//		{
//			pstm.setString(1, doStatus);
//			pstm.setLong(2, atmNumber );
//			int  n = pstm.executeUpdate();
//			Executions.sendRedirect("");
//			
//		}catch(Exception e1)
//		{
//			e1.getMessage();
//		}

	}

	@Listen("onClick=#btnUpdateLimit")
	public void updateLimit() throws NumberFormatException, SQLException {
		Connection con = DBConnection.getConnection();
		
		String sql = "UPDATE cards SET current_limit = ? WHERE card_number_masked = ?";
		PreparedStatement pst=con.prepareStatement(sql);
	   
		String card_number_masked = (String) Sessions.getCurrent().getAttribute("card_number_masked");
        String str=lblDailyLimit.getValue().substring(1);
        System.out.println(str);
		pst.setBigDecimal(1,new BigDecimal(lblDailyLimit.getValue().substring(1)));
		

		pst.setLong(2, Long.parseLong(card_number_masked));
	
		int n=pst.executeUpdate();
	
		if(n!=0)
		{   
			lblCurLimit.setValue(lblDailyLimit.getValue());
			Clients.showNotification(
				    "Daily Limit Updated",
				    Clients.NOTIFICATION_TYPE_INFO,
				    null,
				    "top_center",
				    3000
				);

			
		}
		else
			Clients.showNotification("Failed to update Daily Limit ");
		   }

}
