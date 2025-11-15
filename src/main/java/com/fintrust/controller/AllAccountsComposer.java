package com.fintrust.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.service.AccountServiceImp;
import com.fintrust.service.NomineeServiceImp;
import com.lowagie.text.Anchor;

import java.util.*;

public class AllAccountsComposer extends SelectorComposer<Window> {
	private final AccountServiceImp acconntService = new AccountServiceImp();
	private final NomineeServiceImp nomineeService = new NomineeServiceImp();

    @Wire private Listbox accountListbox;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
      

        Long customerId = (Long)Sessions.getCurrent().getAttribute("customer_id");
        
//        if (customerId == null) {
//            Messagebox.show("Session expired. Please log in again.", "Error", Messagebox.OK, Messagebox.ERROR);
//           // Executions.sendRedirect("/customer_dashboard.zul");
//            return;
//        }

        List<Account> accounts = acconntService.listAllAccounts(customerId);

        for (Account acc : accounts) {
            Listitem item = new Listitem();
            item.appendChild(new Listcell(String.valueOf(acc.getAccount_no())));
            item.appendChild(new Listcell(String.format("%.2f", acc.getBalance())));
            item.appendChild(new Listcell(acc.getAccount_status().name()));
            
            Listcell actions = new Listcell();

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
        Executions.getCurrent().getSession().setAttribute("selected_account_no", acc.getAccount_no());
        // Redirect to details page
        Executions.sendRedirect("/user/account/view_spc_account.zul");
    }

    /** Edit account **/
    private void editAccount(Account acc) {
    	// Store selected account number in session
        Executions.getCurrent().getSession().setAttribute("selected_account_no", acc.getAccount_no());
        // Redirect to details page
        Executions.sendRedirect("/user/account/update_account.zul");
    }

    /** Close account **/
    private void closeAccount(Account acc) {
    	// Store selected account number in session
        Executions.getCurrent().getSession().setAttribute("selected_account_no", acc.getAccount_no());
        // Redirect to details page
        Executions.sendRedirect("/user/account/close_account.zul");
    }

    @Listen("onClick = #backBtn")
    public void onBackClick() {
        Executions.sendRedirect("/user/account/customer_dashboard.zul");
    }
}