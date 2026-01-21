package com.fintrust.cards.controller;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.CardServices;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.AccountDAO;
import com.fintrust.dao.impl.AccountDAOImpl;



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
    private Comboitem dCardItem,cCardItem,pCardItem;
    
    @Wire
    Button submitApplyCard;
    
    CardServices cardService =new CardServices();
    AccountService accountService = new AccountServiceImpl();
    AccountDAO accountDao;
    
     
    @Override
	public  void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		accountDao = new AccountDAOImpl();
		List<Long> accounts = accountService.getAllAccountsNumber();
		accounts.forEach(accountNo -> accountList.appendItem(accountNo+""));		
	}
    
    
    @Listen("onSelect=#accountList")
    public void cardTypeVisible(Event e)
    {   
    	cardCategory.setValue("");
    	dCardItem.setVisible(true);
    	 cCardItem.setVisible(true);
    	 pCardItem.setVisible(true);
    	String selectedAct = accountList.getValue();
    	    
    	List<String> issuedCardForAct=accountDao.issuedCardTypeByAct(Long.parseLong(selectedAct));
       	
    	cardCategory.setButtonVisible(true);
    	
    	
    	issuedCardForAct.forEach(cType->{
    		System.out.println(cType);
    		
    	
    		if(cType.equals("Debit"))
    			dCardItem.setVisible(false);
    		if(cType.equals("Credit"))
    		     cCardItem.setVisible(false);
    		if(cType.equals("prepaid"))
    			  pCardItem.setVisible(false);
    	});
    	
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

            boolean isApplied = cardService.submitCardRequest(accNumber, cardTypes, cardCat, addresss, remark);
            if (isApplied) {
            	NotificationUtil.showInstant("info", "Card request submitted successfully!");          	
            }

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

}

