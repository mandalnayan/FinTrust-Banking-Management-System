package com.fintrust.admin.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.service.AccountServiceImp;
import com.fintrust.service.NomineeServiceImp;

import java.util.*;
import java.util.stream.Collectors;

public class AllAccounts extends SelectorComposer<Window> {
	private final AccountServiceImp acconntService = new AccountServiceImp();
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
        allAccounts = acconntService.listAllAccounts();
        renderAccountList(allAccounts);
    }
    
    private void renderAccountList(List<Account> accounts) {
        accountListbox.getItems().clear();
        for (Account acc : accounts) {
            Listitem item = new Listitem();
            item.appendChild(new Listcell(acc.getAccount_no()+""));
            item.appendChild(new Listcell(acc.getAccount_type().name()));
            item.appendChild(new Listcell(acc.getBalance()+""));
            item.appendChild(new Listcell(acc.getAccount_status().name()));

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

        List<Account> filtered = allAccounts.stream()
                .filter(acc -> (acc.getAccount_no()+"").contains(searchText))
                .collect(Collectors.toList());

        renderAccountList(filtered);
    }

    private void openAccountDetail(Account acc) {
        // Store selected account number in session
        Executions.getCurrent().getSession().setAttribute("selected_account_no", acc.getAccount_no());
        // Redirect to details page
        Executions.sendRedirect("view_spc_account.zul");
    }

    @Listen("onClick = #backBtn")
    public void onBackClick() {
        Executions.sendRedirect("view_all_account.zul");
    }
}