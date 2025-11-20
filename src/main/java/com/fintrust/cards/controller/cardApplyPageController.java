package com.fintrust.cards.controller;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;

import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fintrust.dao.AccountDao;
import com.fintrust.dao.AccountDaoImp;
import com.fintrust.db.DBConnection;
import com.fintrust.service.CardServices;


public class cardApplyPageController extends SelectorComposer<Window>{


    @Wire
    private Combobox accountList;

    @Wire
    private Radiogroup cardType;

    @Wire
    private Combobox cardCategory;

    @Wire
    private Textbox address;

    @Wire
    private Textbox remarks;

    @Wire
    private Checkbox terms;
    
    
    @Wire
    Button submitApplyCard;
    
    CardServices cardService =new CardServices();
    AccountDao accountDao;
    
     
    @Override
	public  void doAfterCompose(Window comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		accountDao = new AccountDaoImp();
		List<Long> accounts= accountDao.getAccountsByUserId(null);
		accounts.forEach(accountNo -> accountList.appendItem(accountNo+""));		
	}

    @Listen("onClick=#submitApplyCard")
    public void submitCardRequest() {
        String accNumber = accountList.getValue();
        String cardTypes = cardType.getSelectedItem().getValue();
        String cardCat = cardCategory.getValue();
        String addresss = address.getValue();
        String remark = remarks.getValue();
        boolean isMarkCheck = terms.isChecked();

        // Validate
        if (accNumber.isEmpty()) {
            Clients.showNotification("⚠️ Please select an Account Number.", "warning", null, "top_center", 3000);
            return;
        }

        if (cardTypes.isEmpty()) {
            Clients.showNotification("⚠️ Please select a Card Type.", "warning", null, "top_center", 3000);
            return;
        }

        if (cardCat.isEmpty()) {
            Clients.showNotification("⚠️ Please select a Card Category.", "warning", null, "top_center", 3000);
            return;
        }

        if (addresss.isEmpty()) {
            Clients.showNotification("⚠️ Please enter your Address.", "warning", null, "top_center", 3000);
            return;
        }

        if (!isMarkCheck) {
            Clients.showNotification("⚠️ Please accept the Terms and Conditions.", "warning", null, "top_center", 3000);
            return;
        }

       

        try {
//            if (cardService.isCardAlreadyRequested(accNumber)) {
//                Clients.showNotification("A card is already requested for this account.", "error", null, "middle_center", 4000);
//                return;
//            }

            cardService.submitCardRequest(accNumber, cardTypes, cardCat, addresss, remark);
            Clients.showNotification("Card request submitted successfully!", "info", null, "top_center", 3000);

            // Clear form
            accountList.setValue("");
            address.setValue("");
            remarks.setValue("");
            terms.setChecked(false);
        } catch (SQLException e) {
            e.printStackTrace();
            Clients.showNotification("Error: " + e.getMessage(), "error", null, "top_center", 4000);
        }
    }

	
//	public static void main(String[] args) {
//	
//		cardApplyPageController ob=new cardApplyPageController();
//		Window w=new Window();
//		try {
//			ob.doAfterCompose(w);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	
//   }
	
}
