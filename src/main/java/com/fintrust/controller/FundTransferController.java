package com.fintrust.controller;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.model.Beneficiary;
import com.fintrust.model.BeneficiaryModel;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.BeneficiaryService;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.AccountDAO;
import com.fintrust.dao.impl.AccountDAOImpl;
import com.fintrust.dao.impl.FundTransferDAO;


public class FundTransferController extends SelectorComposer<Component> {

	@Wire private Combobox accountList, beneficiaryCombo;
	
    @Wire private Longbox toAccount;
    @Wire private Textbox ifsccode;
    @Wire private Doublebox amount;
    @Wire private Label statusLabel;
    
    private Long fromAccount;

    private AccountDAO accountDao;
    private AccountService accountService;
    private BeneficiaryService beneficiaryService;
    
    private List<Beneficiary> beneficiaries;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        accountDao = new AccountDAOImpl();
        accountService = new AccountServiceImpl();
		List<Account> accounts = accountService.getAllAccounts();
		beneficiaryService = new BeneficiaryService();
		
		if (accounts == null || accounts.size() == 0) {
			NotificationUtil.showInstant("error", "Internal server error!");
		} else {
			for (Account account : accounts) {
			    Comboitem item = accountList.appendItem(account.getAccountNumber() + "");
			    item.setValue(account.getAccountNumber());  // <-- THIS FIXES NULL PROBLEM
			}

	        beneficiaries = beneficiaryService.getBeneficiaries();

	        for (Beneficiary b : beneficiaries) {
	            Comboitem item = new Comboitem(b.getName() + " (" + b.getBankName() + ")");
	            item.setValue(b);
	            beneficiaryCombo.appendChild(item);
	        }
		}		
    }
    
    @Listen("onSelect=#accountList")
    public void onAccountSelect() {
    	System.out.println("invoked fundtranser");
        Comboitem selected = accountList.getSelectedItem();
        if (selected != null) {
            fromAccount = selected.getValue();
            System.out.println("acc " + fromAccount);
        }
    }

    @Listen("onSelect=#beneficiaryCombo")
    public void onBeneficiarySelect() {
        Comboitem selected = beneficiaryCombo.getSelectedItem();
        if (selected != null) {
            Beneficiary b = selected.getValue();
            toAccount.setValue(b.getAccountNumber());
            ifsccode.setValue(b.getIfscCode());
          //  Clients.showNotification("Beneficiary selected: " + b.getName());
        }
    }

    @Listen("onClick=#transferBtn")
    public void transferFunds() {
        Long fromAcc = fromAccount;
        Long toAcc = toAccount.getValue();
        Double amt = amount.getValue();
        System.out.println(fromAcc + " To " + toAcc + " amt " + amt);
        if (fromAcc == null || toAcc == null || String.valueOf(toAcc).length() != 12 || amt == null || amt <= 0 || fromAcc == toAcc) {
          
         	NotificationUtil.showInstant("error", "Please enter valid transfer details!");
            return;
        }

        boolean result = FundTransferDAO.transferFunds(fromAcc, toAcc, amt);
        Clients.showNotification("from=" + fromAcc + ", to=" + toAcc + ", amount=" + amt);
        if (result) {
        	NotificationUtil.showInstant("info", "Transfer successfull!");
   		//fromAccount.setValue("");
   		toAccount.setValue(0l);
   		ifsccode.setValue("");
   		amount.setValue(0);

        } else {
        	NotificationUtil.showInstant("error", "Transfer failed! Check balance or account.");
        }
    }
}
