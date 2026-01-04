package com.fintrust.admin.controller;

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
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Include;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.West;

public class AdminDashboardController extends SelectorComposer<Component>{

    // sample properties; replace with actual service calls
    private int pendingCount = 3;
    private int rewardPoints = 1200;
    private int activeCards = 0; 
    
    @Wire Include main_content_sec; 
    @Wire West westNav;
    @Wire Toolbarbutton admindashboard, users, accounts, closeAccount, cards,profile,updateAccount;
    
    private List<Include> includes = new ArrayList<>();
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
    	super.doAfterCompose(comp);
    	Sessions.getCurrent().setAttribute("admin_main_content_sec", main_content_sec);
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

    
    @Listen("onClick=#admindashboard")
    public void adminDashboard() {
 	   main_content_sec.setSrc("/WEB-INF/components/admin_dashboard.zul");
    }
    
    @Listen("onClick=#users")
    public void viewUsers() {
 	   main_content_sec.setSrc("/admin/customerDetails.zul");
    }
    
    @Listen("onClick=#kycRequest")
    public void viewUserKycRequest() {
 	   main_content_sec.setSrc("/admin/userKycRequests.zul");
    }
 
    @Listen("onClick=#accounts")
    public void viewAccount() {
 	   main_content_sec.setSrc("/admin/account/view_all_account.zul");
    }
   
    @Listen("onClick=#updateAccount")
    public void updateAccount() {
	   main_content_sec.setSrc("/admin/account/update_account_customer.zul");
    }
   
    @Listen("onClick=#closeAccount")
    public void closeAccount() {
	   main_content_sec.setSrc("/admin/account/close_account_customer_list.zul"); 
    }
   
    	@Listen("onClick=#cardRequest")
    public void cardReqest() {
	   main_content_sec.setSrc("/admin/adminCards.zul"); 
    }
    	
     	@Listen("onClick=#profile")
        public void profile() {
    	   main_content_sec.setSrc("/WEB-INF/components/adminProfile.zul"); 
        }
   
    // getters for data binding if you bind via MVVM (optional)   
    public int getPendingCount() { return pendingCount; }
    public int getRewardPoints() { return rewardPoints; }
    public int getActiveCards() { return activeCards; }
}
