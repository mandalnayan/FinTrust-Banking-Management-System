package com.fintrust.controller;

import java.security.MessageDigest;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.fintrust.model.AccountCloseRequest;
import com.fintrust.service.AccountCloseRequestService;


public class CloseAccountComposer extends SelectorComposer<Component>{
	@Wire private Label accountNo;
	@Wire private Textbox reason;
	@Wire private Checkbox confirmClose;
	
	private final AccountCloseRequestService closeRequetService = new AccountCloseRequestService();
	
	Long accountNum;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		accountNum = (Long) Executions.getCurrent().getSession().getAttribute("selected_account_no");
	    accountNo.setValue(accountNum+"");
	}
	
	@Listen("onClick=#btnSubmit")
	public void submitCloseAccountRequest() {
		String reasonClose = reason.getValue();
		long accountNo = accountNum;
		
		long customerId;
		//customerId = (Long) Executions.getCurrent().getSession().getAttribute("customer_id");	
		customerId=1001L;
		
		AccountCloseRequest accReq = new AccountCloseRequest();
		accReq.setAccountNo(accountNo);
		accReq.setReason(reasonClose);
		accReq.setRequestedBy(customerId);
		
		if(!confirmClose.isChecked()) {
			Messagebox.show("Please confirm first!");
			return;
		}
		
		if(closeRequetService.saveReq(accReq)) {
			Messagebox.show("Request send Successfully for closing the account");
		}
	}
	
	@Listen("onClick=#btnReset")
	public void resetRequest() {
		Executions.sendRedirect("view_all_account.zul");
	}
}
