package com.fintrust.cards.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.fintrust.db.DBConnection;

public class CardDetailsComposer extends SelectorComposer<Window> {

	@Wire
	Label lblCardNo, lblType, lblMaxLimit, lblStatus, lblCurLimit, lblDailyLimit;

	@Wire
	Button btnBlock, btnUnblock;

	long atmNumber;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);

		String atmCardNumber = (String) Sessions.getCurrent().getAttribute("atmNumber");

		

		Connection connection = DBConnection.getConnection();
		String sql = "select * from card where card_number=?";
		PreparedStatement ptsm = connection.prepareStatement(sql);
		ptsm.setString(1, atmCardNumber);

		ResultSet rs = ptsm.executeQuery();
		while (rs.next()) {

			lblCardNo.setValue(atmCardNumber);
			lblType.setValue("Debit Card");
			lblMaxLimit.setValue("50000");
			lblStatus.setValue(rs.getString("card_status"));

			if (rs.getString("card_status").equalsIgnoreCase("Blocked")) {
				btnBlock.setVisible(false);
				// btnUnblock.setVisible(false);
				// btnBlock.setDisabled(true);
			} else {
				if (rs.getString("card_status").equalsIgnoreCase("Expired")) {

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
		Executions.sendRedirect("/Card/block_unblock_verification.zul");
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
	public void updateLimit() {
		lblCurLimit.setValue(lblDailyLimit.getValue());
		Messagebox.show("Daily Limit Updated.");
	}

}
