package com.fintrust.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.NomineeServiceImp;
import com.fintrust.util.NotificationUtil;
import com.lowagie.text.Anchor;

import java.util.*;

public class AllAccountsComposer extends SelectorComposer<Window> {
	private final AccountServiceImpl acconntService = new AccountServiceImpl();
	private final NomineeServiceImp nomineeService = new NomineeServiceImp();

    @Wire private Listbox accountListbox;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
      

        Long userId = (Long)Sessions.getCurrent().getAttribute("user_id");
        
        if (userId == null) {
            Messagebox.show("Session expired. Please log in again.", "Error", Messagebox.OK, Messagebox.ERROR);
           Executions.sendRedirect("/userDashboard.zul");
            return;
        }

        List<Account> accounts = acconntService.getAllUserAccounts();
        if (accounts == null) return; //No account with this user id
        
        for (Account acc : accounts) {
        	Listitem item = new Listitem();
       	 String accountStatus = acc.getAccount_status().name();

            String bgcolor = "#E8F5E9";
            String border = "4px solid #2E7D32";

            if (accountStatus.equalsIgnoreCase("inactive")) {
                bgcolor = "#FFF8E1";
                border = "4px solid #F9A825";
            } else if (accountStatus.equalsIgnoreCase("closed")) {
                bgcolor = "#FDECEA";
                border = "4px solid #C62828";
            }

            String cellStyle = "background-color:" + bgcolor + ";border-bottom:" + border + ";";

            Listcell c1 = new Listcell(acc.getAccountNumber() + "");
            Listcell c2 = new Listcell(acc.getAccountType().name());
            Listcell c3 = new Listcell(String.format("%.2f", acc.getBalance()));
            Listcell c4 = new Listcell(accountStatus);
            Listcell actions = new Listcell();

            c1.setStyle(cellStyle);
            c2.setStyle(cellStyle);
            c3.setStyle(cellStyle);
            c4.setStyle(cellStyle);
            actions.setStyle(cellStyle);

            item.appendChild(c1);
            item.appendChild(c2);
            item.appendChild(c3);
            item.appendChild(c4);
            

            // View icon
            A viewIcon = new A();
            viewIcon.setIconSclass("z-icon-eye");
            viewIcon.setSclass("action-icon view-icon");
            viewIcon.setTooltiptext("View Account");
            viewIcon.addEventListener("onClick", e -> viewAccount(acc));

            // Edit icon
            A editIcon = new A();
            editIcon.setIconSclass("z-icon-edit");
            editIcon.setSclass("action-icon edit-icon");
            editIcon.setTooltiptext("Edit Account");
            editIcon.addEventListener("onClick", e -> editAccount(acc));

            // Delete icon
            A deleteIcon = new A();
            deleteIcon.setIconSclass("z-icon-trash");
            deleteIcon.setSclass("action-icon delete-icon");
            deleteIcon.setTooltiptext("Delete Account");
            deleteIcon.addEventListener("onClick", e -> closeAccount(acc));

            // Group them horizontally
            Hbox iconBox = new Hbox();
            iconBox.setSpacing("20px");
            iconBox.setWidth("100%");
            iconBox.setPack("center");
            iconBox.appendChild(viewIcon);
            iconBox.appendChild(editIcon);
            iconBox.appendChild(deleteIcon);

            actions.appendChild(iconBox);
            item.appendChild(actions);

            accountListbox.appendChild(item);
        }
    }
    
    /** View account **/
    private void viewAccount(Account acc) {
    	// Store selected account number in session
        Executions.getCurrent().getSession().setAttribute("selected_account_no", acc.getAccountNumber());
        // Redirect to details page
        
        //Find center area in dashboard
        Include centerArea = (Include) getPage().getFellow("main_content_sec");

        // Load details page
        centerArea.setSrc("/user/account/view_spc_account.zul");
    }

    /** Edit account **/
    private void editAccount(Account acc) {
//    	Checking status of account. Updation not happen if account is closed or inactive
    		String accountStatus = acc.getAccount_status().name();
        if (!accountStatus.equals("ACTIVE")) {
        	NotificationUtil.showInstant("warning", "Your account is " + accountStatus + ".\n you can't update account");
        	return;
        }
        
     // Store selected account number in session
    	Executions.getCurrent().getSession().setAttribute("selected_account_no", acc.getAccountNumber());
       
        Include centerArea = (Include) getPage().getFellow("main_content_sec");
        centerArea.setSrc("/user/account/update_account.zul");
    }

    /** Close account **/
    private void closeAccount(Account acc) {
    	String accountStatus = acc.getAccount_status().name();
        if (!accountStatus.equals("ACTIVE")) {
        	NotificationUtil.showInstant("warning", "Your account is " + accountStatus + ". you can't delete account");
        	return;
        }
    	
    	// Store selected account number in session
        Executions.getCurrent().getSession().setAttribute("selected_account_no", acc.getAccountNumber());
        // Redirect to details page
       
        Include centerArea = (Include) getPage().getFellow("main_content_sec");
        centerArea.setSrc("/user/account/close_account.zul");
    }

    @Listen("onClick = #backBtn")
    public void onBackClick() {
        Executions.sendRedirect("/user/account/customer_dashboard.zul");
    }
}