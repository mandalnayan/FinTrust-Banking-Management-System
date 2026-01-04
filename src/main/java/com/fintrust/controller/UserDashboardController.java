package com.fintrust.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Include;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.West;

public class UserDashboardController extends SelectorComposer<Component> {

	// sample properties; replace with actual service calls
	private int pendingCount = 3;
	private int rewardPoints = 1200;
	private int activeCards = 0;

	@Wire
	Include main_content_sec;

	 @Wire
	 private West westNav;
	
	@Wire
	Toolbarbutton userdashboard, profile, account, viewAccounts, kyc, transactions;

	private List<Include> includes = new ArrayList<>();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		Sessions.getCurrent().setAttribute("main_content_sec", main_content_sec);
	}
	
	/**
	 * For closing side bar in small screen
	 * @param event
	 */
	 @Listen("onClientInfo = #main")
	    public void onClientInfo(ClientInfoEvent event) {

	        int screenWidth = event.getDesktopWidth(); // ✅ CORRECT
	      
	        if (screenWidth <= 800) {
	            westNav.setOpen(false);   // mobile
	        } else {
	            westNav.setOpen(true);    // desktop
	        }
	    }


	@Listen("onClick=#home")
	public void home() {
		Session session = Sessions.getCurrent();
		Object userRole = session.getAttribute("user_id");
		if (userRole != null) {
			Executions.sendRedirect("/user/userDashboard.zul");
		} else {
			Executions.sendRedirect("/admin/adminDashboard.zul");
		}
		
	}
	
	// commands wired from ZUL
	@Listen("onClick=#logout")
	public void logout() {
		Session session = Sessions.getCurrent();
		session.removeAttribute("user");
		session.removeAttribute("user_email");
		session.removeAttribute("user_name");
		session.removeAttribute("user_id");
		session.removeAttribute("");
		session.invalidate();
		Executions.sendRedirect("/logout");
	}

	@Listen("onClick=#userdashboard")
	public void dashboard() {
		main_content_sec.setSrc("/WEB-INF/components/dashboard.zul");

		userdashboard.addSclass("active");
		account.removeSclass("active");
		viewAccounts.removeSclass("active");
		profile.removeSclass("active");
		kyc.removeSclass("active");
	}

	@Listen("onClick=#account")
	public void openAccount() {
		main_content_sec.setSrc("/WEB-INF/components/openNewAccount.zul");

		account.addSclass("active");
		userdashboard.removeSclass("active");
		viewAccounts.removeSclass("active");
		profile.removeSclass("active");
		kyc.removeSclass("active");
	}

	@Listen("onClick=#kyc")
	public void openKyc() {
		main_content_sec.setSrc("/WEB-INF/components/kycForm.zul");

		kyc.addSclass("active");
		account.removeSclass("active");
		userdashboard.removeSclass("active");
		viewAccounts.removeSclass("active");
		profile.removeSclass("active");
	}

	@Listen("onClick=#viewAccounts")
	public void viewAccount() {
		main_content_sec.setSrc("/WEB-INF/components/view_all_account.zul");

		viewAccounts.addSclass("active");
		userdashboard.removeSclass("active");
		account.removeSclass("active");
		profile.removeSclass("active");
		kyc.removeSclass("active");
	}

	@Listen("onClick=#cards")
	public void cards() {
		Executions.sendRedirect("/user/card/cardHome.zul");
	}

	@Listen("onClick=#profile")
	public void profile() {
		main_content_sec.setSrc("/WEB-INF/components/userProfile.zul");

		profile.addSclass("active");
		viewAccounts.removeSclass("active");
		userdashboard.removeSclass("active");
		account.removeSclass("active");
		kyc.removeSclass("active");
	}

	@Listen("onClick=#fundTransfer")
	public void fundTransfer() {
		main_content_sec.setSrc("/WEB-INF/components/fundTransfer.zul");

		profile.removeSclass("active");
		viewAccounts.removeSclass("active");
		userdashboard.removeSclass("active");
		account.removeSclass("active");
	}

	@Listen("onClick=#fundTransferIcon")
	public void fundTransferIcon() {
		if (main_content_sec == null)
			main_content_sec = (Include) Sessions.getCurrent().getAttribute("main_content_sec");
		//main_content_sec.setSrc("/WEB-INF/components/fundTransfer.zul");
		
	}

	@Listen("onClick=#transactions")
	public void transactions() {
		main_content_sec.setSrc("/WEB-INF/components/transactionHistory.zul");
		transactions.addSclass("active");
		profile.removeSclass("active");
		viewAccounts.removeSclass("active");
		userdashboard.removeSclass("active");
		account.removeSclass("active");
	}

	/*
	 * @Command public void go(String page) { // navigate based on menu clicks
	 * switch (page) { case "dashboard": //
	 * org.zkoss.zk.ui.Executions.sendRedirect("/user/userDashboard.zul");
	 * profile.setVisible(false); userdashboard.setVisible(true); break; case
	 * "accounts":
	 * org.zkoss.zk.ui.Executions.sendRedirect("/user/userAccounts.zul"); break;
	 * case "transactions":
	 * org.zkoss.zk.ui.Executions.sendRedirect("/user/transactionHistory.zul");
	 * break; case "cards":
	 * org.zkoss.zk.ui.Executions.sendRedirect("/user/userCards.zul"); break; case
	 * "profile": //
	 * org.zkoss.zk.ui.Executions.sendRedirect("/user/userProfile.zul");
	 * profile.setVisible(true); userdashboard.setVisible(false); break; case
	 * "transfer": org.zkoss.zk.ui.Executions.sendRedirect("/user/transfer.zul");
	 * break; case "payBill":
	 * org.zkoss.zk.ui.Executions.sendRedirect("/user/payBill.zul"); break; default:
	 * break; } }
	 * 
	 * @Command public void support() {
	 * org.zkoss.zk.ui.Executions.sendRedirect("/contact.zul"); }
	 */

	// getters for data binding if you bind via MVVM (optional)
	public int getPendingCount() {
		return pendingCount;
	}

	public int getRewardPoints() {
		return rewardPoints;
	}

	public int getActiveCards() {
		return activeCards;
	}
}
