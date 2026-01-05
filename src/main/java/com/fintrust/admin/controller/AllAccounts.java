package com.fintrust.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.service.AccountServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for displaying all accounts
 * and navigating to single account details.
 */
public class AllAccounts extends SelectorComposer<Window> {
    private static final long serialVersionUID = 2492592400378886456L;

	private static final Logger logger = LoggerFactory.getLogger(AllAccounts.class);

	private final AccountServiceImpl acconntService = new AccountServiceImpl();
	
	@Wire Textbox searchBox; 
    @Wire Listbox accountListbox;
    
    private List<Account> allAccounts;

    /**
     * Called after ZUL components are composed.
     *
     * @param comp main window component
     * @throws Exception if any UI error occurs
     */
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        logger.info("AllAccounts page initialized");
        loadAccounts();
    }

    /**
     * Loads all accounts from service layer.
     */
    private void loadAccounts() {
        logger.debug("Loading all accounts");
        allAccounts = acconntService.getAllAccounts();
        logger.info("Total accounts loaded: {}", 
                allAccounts != null ? allAccounts.size() : 0);
        renderAccountList(allAccounts);
    }

    /**
     * Renders the account list in the listbox.
     *
     * @param allAccounts list of accounts to display
     */
    private void renderAccountList(List<Account> allAccounts) {

        accountListbox.getItems().clear();

        if (allAccounts == null || allAccounts.isEmpty()) {
            logger.warn("No accounts available to render");
            return;
        }

        for (Account acc : allAccounts) {

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

             c1.setStyle(cellStyle);
             c2.setStyle(cellStyle);
             c3.setStyle(cellStyle);
             c4.setStyle(cellStyle);

             item.appendChild(c1);
             item.appendChild(c2);
             item.appendChild(c3);
             item.appendChild(c4);

             item.setValue(acc);
             item.addEventListener("onClick", e -> openAccountDetail(acc));

             accountListbox.appendChild(item);
        }

        logger.debug("Account list rendered successfully");
    }

    /**
     * Handles search button click event.
     */
    @Listen("onClick = #searchBtn")
    public void onSearch() {
        String searchText = searchBox.getValue().trim().toLowerCase();
        logger.debug("Search triggered with value: {}", searchText);

        if (searchText.isEmpty()) {
            renderAccountList(allAccounts);
            return;
        }

        List<Account> filtered = allAccounts.stream()
                .filter(acc -> (acc.getAccountNumber() + "").contains(searchText))
                .collect(Collectors.toList());

        logger.info("Search result count: {}", filtered.size());
        renderAccountList(filtered);
    }

    /**
     * Opens the selected account detail page.
     *
     * @param acc selected account
     */
    private void openAccountDetail(Account acc) {
        if (acc == null) {
            logger.warn("Selected account is null");
            return;
        }

        logger.info("Opening details for account number: {}", acc.getAccountNumber());

        Executions.getCurrent().getSession().setAttribute("selected_account_no", acc.getAccountNumber());
        Include centerArea = (Include) getPage().getFellow("main_content_sec");
        centerArea.setSrc("/admin/account/view_spc_account.zul");
    }

    /**
     * Handles back button click event.
     */
    @Listen("onClick = #backBtn")
    public void onBackClick() {
        logger.info("Back button clicked, redirecting to account list");
        Executions.sendRedirect("view_all_account.zul");
    }
}
