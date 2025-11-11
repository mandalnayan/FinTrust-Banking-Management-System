package com.fintrust.controller;

import java.util.HashMap;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;

public class UserDashboardController extends SelectorComposer<Borderlayout>{

    // sample properties; replace with actual service calls
    private String availableBalance = "₹ 1,25,000.00";
    private String defaultAccount = "Savings - 1234567890";
    private int pendingCount = 3;
    private int rewardPoints = 1200;
    private int activeCards = 2; 

    // commands wired from ZUL
   @Listen("onClick=#logout")
    public void logout() {
	   Session session = Sessions.getCurrent();
	   session.removeAttribute("currentUser");
	   session.invalidate();
        org.zkoss.zk.ui.Executions.sendRedirect("/user/login.zul");
    }

    @Command
    public void go(String page) {
        // navigate based on menu clicks
    	alert("HIi");
        switch (page) {
            case "dashboard":
                org.zkoss.zk.ui.Executions.sendRedirect("/user/userDashboard.zul");
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
                org.zkoss.zk.ui.Executions.sendRedirect("/user/userProfile.zul");
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

    // getters for data binding if you bind via MVVM (optional)
    public String getAvailableBalance() { return availableBalance; }
    public String getDefaultAccount() { return defaultAccount; }
    public int getPendingCount() { return pendingCount; }
    public int getRewardPoints() { return rewardPoints; }
    public int getActiveCards() { return activeCards; }
}
