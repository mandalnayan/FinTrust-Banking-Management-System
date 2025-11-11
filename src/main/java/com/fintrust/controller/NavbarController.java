package com.fintrust.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class NavbarController extends SelectorComposer<Div>{

	@Wire Label homeId, accountsId, transactionsId, cardsId, signupId, loginId;
	
	@Listen("onClick=#loginId")
	public void login() {
		Executions.sendRedirect("/user/login.zul");
		}
	
	@Listen("onClick=#aboutId")
	public void about() {
		Executions.sendRedirect("/about.zul");
	}
	
	@Listen("onClick = #transactionsId")
	public void viewTransactions() {
		Executions.sendRedirect("/user/transactionHistory.zul");
	}
	
	private void toggleNavItem(boolean flag) {
//			Account button
		cardsId.setVisible(flag);
		transactionsId.setVisible(flag);
		if (flag) {
//			Home button
			accountsId.setValue("Accounts");
			signupId.setValue("Profile");
			signupId.addEventListener("onClick", (event) -> Executions.sendRedirect("/user/userView.zul"));
			loginId.setValue("Logout");
			accountsId.addEventListener("onClick", (event) -> Executions.sendRedirect("/user/userAccount.zul"));	
		}
		else {			
			accountsId.setValue("Open Account");
			signupId.addEventListener("onClick", (event) -> Executions.sendRedirect("/user/userSignup.zul"));
			loginId.addEventListener("onClick", (event) -> Executions.sendRedirect("/user/userLogin.zul"));
		}
		
	}
	
}
