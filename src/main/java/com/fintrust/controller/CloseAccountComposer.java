package com.fintrust.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.fintrust.service.AccountServiceImp;


public class CloseAccountComposer extends SelectorComposer<Component>{
	@Wire private Label accountNo;
	@Wire private Textbox reason;
	private final AccountServiceImp acconntService = new AccountServiceImp();
	
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
	}
}
