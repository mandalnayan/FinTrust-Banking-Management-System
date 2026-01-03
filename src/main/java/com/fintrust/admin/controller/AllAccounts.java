package com.fintrust.admin.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.NomineeServiceImp;

import java.util.*;
import java.util.stream.Collectors;

public class AllAccounts extends SelectorComposer<Window> {
	private final AccountServiceImpl acconntService = new AccountServiceImpl();
	private final NomineeServiceImp nomineeService = new NomineeServiceImp();
	
	@Wire
	private Textbox searchBox; 
	
    @Wire 
    private Listbox accountListbox;
    
    private List<Account> allAccounts;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        loadAccounts();
    }
    

    /** Load all accounts initially **/
    private void loadAccounts() {
        allAccounts = acconntService.getAllAccounts();
        renderAccountList(allAccounts);
    }
    
    private void renderAccountList(List<Account> allAccounts) {
    	System.out.println(allAccounts);
        accountListbox.getItems().clear();
        for (Account acc : allAccounts) {
            Listitem item = new Listitem();
            item.appendChild(new Listcell(acc.getAccountNumber()+""));
            item.appendChild(new Listcell(acc.getAccountType().name()));
            item.appendChild(new Listcell(acc.getBalance()+""));
            String accountStatus = acc.getAccount_status().name();
            
            item.appendChild(new Listcell(accountStatus));

            String bgcolor = "#E8F5E9", border = "4px solid #2E7D32";
            if (accountStatus.equalsIgnoreCase("inactive")) {
            	bgcolor = "#FFF8E1";
            	border = "4px solid #F9A825";
            } else if(accountStatus.equalsIgnoreCase("inactive")) {
            	bgcolor= "#FDECEA";   /* light red */
            	border = "4px solid #C62828";
            }
            item.setStyle("background-color:" + bgcolor + ";border-left:" + border);
            // store account object as value
            item.setValue(acc);

            // make item clickable
            item.addEventListener("onClick", e -> openAccountDetail(acc));

            accountListbox.appendChild(item);
        }
        
    }
    
    /** Search event **/
    @Listen("onClick = #searchBtn")
    public void onSearch() {
        String searchText = searchBox.getValue().trim().toLowerCase();

        if (searchText.isEmpty()) {
            renderAccountList(allAccounts);
            return;
        }

        List<com.fintrust.model.Account> filtered = allAccounts.stream()
                .filter(acc -> (acc.getAccountNumber()+"").contains(searchText))
                .collect(Collectors.toList());

        renderAccountList(filtered);
    }

    private void openAccountDetail(com.fintrust.model.Account acc) {
        // Store selected account number in session
        Executions.getCurrent().getSession().setAttribute("selected_account_no", acc.getAccountNumber());
        // Redirect to details page
        Include centerArea = (Include) getPage().getFellow("main_content_sec");
        centerArea.setSrc("/admin/account/view_spc_account.zul");
    }

    @Listen("onClick = #backBtn")
    public void onBackClick() {
        Executions.sendRedirect("view_all_account.zul");
    }
}