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
import com.fintrust.model.BeneficiaryModel;
import com.fintrust.dao.AccountDao;
import com.fintrust.dao.AccountDaoImp;
import com.fintrust.dao.BeneficiaryDao;
import com.fintrust.dao.FundTransferDao;


public class FundTransferController extends SelectorComposer<Component> {

	@Wire private Combobox accountList;
	
    @Wire private Textbox toAccount;
    @Wire private Textbox ifsccode;
    @Wire private Doublebox amount;
    @Wire private Combobox beneficiaryCombo;
    @Wire private Label statusLabel;
    
    private Long fromAccount;

    AccountDao accountDao;
    private List<BeneficiaryModel> beneficiaries;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        accountDao = new AccountDaoImp();
		List<Long> accounts = accountDao.getAccountsByUserId(null);
		
		if (accounts == null) {
			Clients.showNotification("Internal server error!", "error", null, "top_center", 3000);
		} else if(accounts.isEmpty()) {
			Clients.showNotification("Please open accounts before transfer money...!", "warning", null, "top_center", 3000);
		}else {
			accounts.forEach(accountNo -> accountList.appendItem(accountNo+""));
		    
	        Long userId = (Long)Sessions.getCurrent().getAttribute("customer_id"); 
	        alert(userId + "");
	        
	        beneficiaries = BeneficiaryDao.getBeneficiariesByUserId(userId);

	        for (BeneficiaryModel b : beneficiaries) {
	            Comboitem item = new Comboitem(b.getName() + " (" + b.getBankName() + ")");
	            item.setValue(b);
	            beneficiaryCombo.appendChild(item);
	        }
		}
		
        
    }
    
    @Listen("onSelect=#accountList")
    public void onAccountSelect() {
        Comboitem selected = accountList.getSelectedItem();
        if (selected != null) {
            Account account = selected.getValue();
            fromAccount = account.getAccount_no();
        }
    }

    @Listen("onSelect=#beneficiaryCombo")
    public void onBeneficiarySelect() {
        Comboitem selected = beneficiaryCombo.getSelectedItem();
        if (selected != null) {
            BeneficiaryModel b = selected.getValue();
            toAccount.setValue(b.getAccountNumber());
            ifsccode.setValue(b.getIfscCode());
            Clients.showNotification("Beneficiary selected: " + b.getName());
        }
    }

    @Listen("onClick=#transferBtn")
    public void transferFunds() {
        Long fromAcc = fromAccount;
        String toAcc = toAccount.getValue();
        Double amt = amount.getValue();

        if (fromAcc != null || toAcc.isEmpty() || amt == null || amt <= 0) {
            Clients.showNotification("Please enter valid transfer details!", "error", null, "top_center", 3000);

            return;
        }

        boolean result = FundTransferDao.transferFunds(fromAcc, toAcc, amt);
        Clients.showNotification("from=" + fromAcc + ", to=" + toAcc + ", amount=" + amt);
        if (result) {
   		 Clients.showNotification("Transfer successfull!", "info", null, "top_center", 3000);
   		fromAccount.setValue("");
   		toAccount.setValue("");
   		ifsccode.setValue("");
   		amount.setValue(0);

        } else {
            Clients.showNotification("Transfer failed! Check balance or account.", "error", null, "top_center", 3000);
        }
    }
}
