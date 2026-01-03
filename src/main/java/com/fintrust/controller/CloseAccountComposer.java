package com.fintrust.controller;

import java.security.MessageDigest;
import java.sql.SQLException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.fintrust.model.AccountCloseRequest;
import com.fintrust.service.AccountCloseRequestService;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;


public class CloseAccountComposer extends SelectorComposer<Component>{
	private static final long serialVersionUID = 1L;
	
	@Wire private Label accountNo;
	@Wire private Textbox reason;
	@Wire private Checkbox confirmClose;
	
	private final AccountCloseRequestService closeRequetService = new AccountCloseRequestService();
	
	private Long accountNum;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		accountNum = (Long) Executions.getCurrent().getSession().getAttribute("selected_account_no");
	    accountNo.setValue(accountNum+"");
	}
	
	@Listen("onClick=#btnSubmit")
	public void submitCloseAccountRequest() throws SQLException {
		String reasonClose = reason.getValue();
		long accountNo = accountNum;
		long userId = (long) Sessions.getCurrent().getAttribute("user_id");
		
		AccountCloseRequest accReq = new AccountCloseRequest();
		accReq.setAccountNo(accountNo);
		accReq.setReason(reasonClose);
		accReq.setRequestedBy(new UserServiceImpl().getUserByUserId(userId));
		
		if(!confirmClose.isChecked()) {
			NotificationUtil.showInstant("warning", "Please confirm first!");
			return;
		}
		
		if(closeRequetService.saveReq(accReq)) {
			NotificationUtil.showInstant("info", "Request send Successfully for closing the account");
			
			Component root = getSelf();
			Include inc = (Include) root.getPage().getFellow("main_content_sec");
			inc.setSrc("/WEB-INF/components/view_all_account.zul");
		} 
		else {
			NotificationUtil.showInstant("warning", "Request is already submitted for closing the account");
			
			Component root = getSelf();
			Include inc = (Include) root.getPage().getFellow("main_content_sec");
			inc.setSrc("/WEB-INF/components/view_all_account.zul");
		}
	}
	
	@Listen("onClick=#btnReset")
	public void resetRequest() {
		Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");
		inc.setSrc("/WEB-INF/components/view_all_account.zul");
	}
}
