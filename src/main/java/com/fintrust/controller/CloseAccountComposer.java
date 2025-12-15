package com.fintrust.controller;

import java.security.MessageDigest;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Include;
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
		long userId = (long) Sessions.getCurrent().getAttribute("user_id");
		
		AccountCloseRequest accReq = new AccountCloseRequest();
		accReq.setAccountNo(accountNo);
		accReq.setReason(reasonClose);
		accReq.setRequestedBy(userId);
		
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
		//Executions.sendRedirect("/user/userDashboard.zul");
		Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");
		inc.setSrc("/WEB-INF/components/view_all_account.zul");
	}
}
