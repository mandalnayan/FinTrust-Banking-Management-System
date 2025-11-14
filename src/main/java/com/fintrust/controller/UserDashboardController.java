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
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Include;
import org.zkoss.zul.Toolbarbutton;

public class UserDashboardController extends SelectorComposer<Borderlayout>{

    // sample properties; replace with actual service calls
    private String availableBalance = "₹ 1,25,000.00";
    private String defaultAccount = "Savings - 1234567890";
    private int pendingCount = 3;
    private int rewardPoints = 1200;
    private int activeCards = 2; 
    
    @Wire Include userdashboard_sec, profile_sec, changePassword_sec, openAccount_sec, viewAllAccount_sec; 
    
    @Wire Toolbarbutton userdashboard, profile, account, viewAccounts;
    
    private List<Include> includes = new ArrayList<>();
    private List<Toolbarbutton> buttons = new ArrayList<>();
    
    private void initialize() {
    	if (!includes.isEmpty()) return;
    	includes.add(userdashboard_sec);
		includes.add(profile_sec);
		includes.add(changePassword_sec);
		includes.add(openAccount_sec);
		includes.add(viewAllAccount_sec);
		
		buttons.add(userdashboard);
		buttons.add(profile);
		buttons.add(account);
		buttons.add(viewAccounts);
    }
    
    // commands wired from ZUL
   @Listen("onClick=#logout")
    public void logout() {
	   Session session = Sessions.getCurrent();
	   session.removeAttribute("currentUser");
	   session.invalidate();
       org.zkoss.zk.ui.Executions.sendRedirect("/home.zul");
    }   
   
   @Listen("onClick=#userdashboard")
   public void dashboard() {	   
	   userdashboard_sec.setVisible(true);
	   profile_sec.setVisible(false);
	   changePassword_sec.setVisible(false);
	   openAccount_sec.setVisible(false);
	   viewAllAccount_sec.setVisible(false);
	   
	   userdashboard.addSclass("active");
	   account.removeSclass("active");
	   viewAccounts.removeSclass("active");
	   profile.removeSclass("active");
   }
   
   @Listen("onClick=#account")
   public void openAccount() {
	   openAccount_sec.setVisible(true);
	   userdashboard_sec.setVisible(false);
	   profile_sec.setVisible(false);
	   changePassword_sec.setVisible(false);
	   viewAllAccount_sec.setVisible(false);
	   
	   userdashboard.removeSclass("active");
	   account.addSclass("active");
	   viewAccounts.removeSclass("active");
	   profile.removeSclass("active");
   }
   
   @Listen("onClick=#viewAccounts")
   public void viewAccount() {
	   viewAllAccount_sec.setVisible(true);
	   openAccount_sec.setVisible(false);
	   userdashboard_sec.setVisible(false);
	   profile_sec.setVisible(false);
	   changePassword_sec.setVisible(false);
	   
	   viewAccounts.addSclass("active");
	   userdashboard.removeSclass("active");
	   account.removeSclass("active");
	   profile.removeSclass("active");
   }
   
   @Listen("onClick=#profile")
   public void profile() {
	   profile_sec.setVisible(true);
	   viewAllAccount_sec.setVisible(false);
	   openAccount_sec.setVisible(false);
	   userdashboard_sec.setVisible(false);
	   changePassword_sec.setVisible(false);
	   
	   profile.addSclass("active");
	   viewAccounts.removeSclass("active");
	   userdashboard.removeSclass("active");
	   account.removeSclass("active");
   }
   
  @Listen("onClick=#transactions")
  public void transactions() {
	  Executions.sendRedirect("transactionHistory.zul");
  }
  
  @Listen("onClick=#cards")
  public void cards() {
	  Executions.sendRedirect("/Card/cardHome.zul");
  }
   
   /*
    @Command
    public void go(String page) {
        // navigate based on menu clicks
        switch (page) {
            case "dashboard":
               // org.zkoss.zk.ui.Executions.sendRedirect("/user/userDashboard.zul");
                profile.setVisible(false);
                userdashboard.setVisible(true);
                break;
            case "accounts":
                org.zkoss.zk.ui.Executions.sendRedirect("/user/userAccounts.zul");
                break;
            case "transactions":
                org.zkoss.zk.ui.Executions.sendRedirect("/user/transactionHistory.zul");
                break;
            case "cards":
                org.zkoss.zk.ui.Executions.sendRedirect("/user/userCards.zul");
                break;
            case "profile":
               // org.zkoss.zk.ui.Executions.sendRedirect("/user/userProfile.zul");
                profile.setVisible(true);
                userdashboard.setVisible(false);
                break;
            case "transfer":
                org.zkoss.zk.ui.Executions.sendRedirect("/user/transfer.zul");
                break;
            case "payBill":
                org.zkoss.zk.ui.Executions.sendRedirect("/user/payBill.zul");
                break;
            default:
                break;
        }
    }

    @Command
    public void support() {
        org.zkoss.zk.ui.Executions.sendRedirect("/contact.zul");
    }
*/
   
   private void toggleVisibility(Include activeSection, Toolbarbutton button) {
	   includes.forEach((section) -> {
		   section.setVisible(section.equals(activeSection));			   
	   });
	   buttons.forEach((btn) -> {
		   if (btn.getLabel().equals(button.getLabel())) {
			   button.addSclass("active");
		   } else {
			   button.removeSclass("active");
		   }
	   });
   }
    // getters for data binding if you bind via MVVM (optional)
    public String getAvailableBalance() { return availableBalance; }
    public String getDefaultAccount() { return defaultAccount; }
    public int getPendingCount() { return pendingCount; }
    public int getRewardPoints() { return rewardPoints; }
    public int getActiveCards() { return activeCards; }
}
