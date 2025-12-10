package com.fintrust.controller;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import com.fintrust.model.Account;
import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.RequestUpdateService;
import com.fintrust.util.NotificationUtil;
import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
import com.fintrust.dao.impl.AccountUpdateRequestDao;

public class UpdateAccountRequest extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;	
	
	@Wire private Label accountNo, accountBalance,accountStatus;
	@Wire private Combobox accountType, accountBranch , accountMode;
	
	private final AccountServiceImpl acconntService = new AccountServiceImpl();
	Long accountNum;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		accountNum = (Long) Executions.getCurrent().getSession().getAttribute("selected_account_no");
		
        Account acc = acconntService.getAccountDetails(accountNum);
        System.out.println("Acc No: " + acc.getAccountNumber());
        accountNo.setValue(acc.getAccountNumber()+"");
        accountBalance.setValue(acc.getBalance()+"");
        accountStatus.setValue(acc.getStatus().name());
    
	}
	
/**
 * Taking updatable data
 */
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
         System.out.println("Updating: " + accountNum);
         Long user_id = (Long) Executions.getCurrent().getSession().getAttribute("user_id");
         req.setRequestedBy(user_id);

         if(new RequestUpdateService().updateRequest(req)) {
        	 		NotificationUtil.push("info", "Requested submitted successfully.\n your requested will be handled instantly.");
        	 		Executions.sendRedirect("view_all_account.zul");
         } else {
        	 	NotificationUtil.showInstant("warning", "Requested is already submitted. Please check status");
         }
	}
	
	@Listen("onClick=#cancel")
	public void cancelUpdateAccountReq() {
		//Executions.sendRedirect("/user/userDashboard.zul");
		Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");
		inc.setSrc("/WEB-INF/components/view_all_account.zul");
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