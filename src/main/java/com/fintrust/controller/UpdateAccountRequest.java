package com.fintrust.controller;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import com.fintrust.dao.AccountUpdateRequestDao;
import com.fintrust.model.Account;
import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.service.AccountServiceImp;

public class UpdateAccountRequest extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	
	@Wire private Label accountNo,accountBalance,accountStatus;
	@Wire private Combobox accountType, accountBranch , accountMode;
	
	private final AccountServiceImp acconntService = new AccountServiceImp();
	Long accountNum;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		accountNum = (Long) Executions.getCurrent().getSession().getAttribute("selected_account_no");
		
        Account acc = acconntService.getAccountDetails(accountNum);
        accountNo.setValue(acc.getAccount_no()+"");
        accountBalance.setValue(acc.getBalance()+"");
        accountStatus.setValue(acc.getAccount_status().name());
    
	}
	

	
	@Listen("onClick=#update")
	public void sendUpdateAccountReq() {
		 if (!isFormValid()) return;
		
		 //Messagebox.show("Request submitted successfully!");
		 String accType = accountType.getSelectedItem().getValue();
		 String accBranch = accountBranch.getSelectedItem().getValue();
		 String accMode = accountMode.getSelectedItem().getValue(); 
		
		 AccountUpdateRequest req = new AccountUpdateRequest();
         req.setAccountNo(accountNum);
         req.setNewAccountType(accType);
         req.setNewBranchName(accBranch);
         req.setNewModeOfOperation(accMode);
         
         Long customerId = (Long) Executions.getCurrent().getSession().getAttribute("customer_id");
         customerId=1001L;
         req.setRequestedBy(customerId);

         if(new AccountUpdateRequestDao().save(req)) {
        	 Messagebox.show("Request submitted successfully!");
             //Executions.sendRedirect("view_all_account.zul");
         }
	}
	
	@Listen("onClick=#cancel")
	public void cancelUpdateAccountReq() {
		Executions.sendRedirect("view_all_account.zul");
	}
	
	public boolean isFormValid(){
		if(accountType.getSelectedItem() == null) {
			showWarning("Please select Account Type.");
            return false;
		}
		
		if(accountBranch.getSelectedItem() == null) {
			showWarning("Please select Branch.");
            return false;
		}
		
		if(accountMode.getSelectedItem() == null) {
			showWarning("Please select Mode of Operation.");
            return false;
		}
		return true;
	}
	
	//Helper to show warning messages
    private void showWarning(String msg) {
        Messagebox.show(msg, "Validation Error", Messagebox.OK, Messagebox.EXCLAMATION);
    }
}