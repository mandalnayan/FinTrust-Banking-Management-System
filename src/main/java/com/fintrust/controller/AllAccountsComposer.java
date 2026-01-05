package com.fintrust.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.util.NotificationUtil;

/**
 * Controller responsible for displaying all accounts
 * of the logged-in user and handling account actions
 * like view, edit, and close.
 *
 */
public class AllAccountsComposer extends SelectorComposer<Window> {

    private static final long serialVersionUID = -8185683909710066352L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AllAccountsComposer.class);

    private static final String SESSION_USER_ID = "user_id";
    private static final String SESSION_SELECTED_ACCOUNT = "selected_account_no";

    private final AccountServiceImpl accountService = new AccountServiceImpl();
    
    /** Navigation constants */
    private static final String DASHBOARD_ZUL = "/userDashboard.zul";
    private static final String VIEW_ACCOUNT_ZUL = "/user/account/view_spc_account.zul";
    private static final String UPDATE_ACCOUNT_ZUL = "/user/account/update_account.zul";
    private static final String CLOSE_ACCOUNT_ZUL = "/user/account/close_account.zul";
    private static final String CUSTOMER_DASHBOARD_ZUL = "/user/account/customer_dashboard.zul";

    @Wire Listbox accountListbox;

    /**
     * Initializes the UI after component composition.
     *
     * @param comp the root window component
     * @throws Exception if UI initialization fails
     */
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        Long userId = (Long) Sessions.getCurrent().getAttribute(SESSION_USER_ID);

        if (userId == null) {
            LOGGER.warn("Session expired or user not logged in.");
            Messagebox.show(
                    "Session expired. Please log in again.",
                    "Error",
                    Messagebox.OK,
                    Messagebox.ERROR
            );
            Executions.sendRedirect(DASHBOARD_ZUL);
            return;
        }

        LOGGER.info("Loading accounts for userId={}", userId);

        List<Account> accounts = accountService.getAllUserAccounts();

        if (accounts == null || accounts.isEmpty()) {
            LOGGER.info("No accounts found for userId={}", userId);
            return;
        }

        for (Account account : accounts) {
            renderAccountRow(account);
        }
    }

    /**
     * Renders a single account row in the listbox.
     *
     * @param account the account entity to display
     */
    private void renderAccountRow(final Account account) {

        Listitem item = new Listitem();

        String accountStatus = account.getAccount_status().name();

        String bgcolor = "#E8F5E9";
        String border = "4px solid #2E7D32";

        if ("inactive".equalsIgnoreCase(accountStatus)) {
            bgcolor = "#FFF8E1";
            border = "4px solid #F9A825";
        } else if ("closed".equalsIgnoreCase(accountStatus)) {
            bgcolor = "#FDECEA";
            border = "4px solid #C62828";
        }

        String cellStyle =
                "background-color:" + bgcolor + ";border-bottom:" + border + ";";

        Listcell c1 = new Listcell(String.valueOf(account.getAccountNumber()));
        Listcell c2 = new Listcell(account.getAccountType().name());
        Listcell c3 = new Listcell(String.format("%.2f", account.getBalance()));
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
        viewIcon.addEventListener("onClick", e -> viewAccount(account));

        // Edit icon
        A editIcon = new A();
        editIcon.setIconSclass("z-icon-edit");
        editIcon.setSclass("action-icon edit-icon");
        editIcon.setTooltiptext("Edit Account");
        editIcon.addEventListener("onClick", e -> editAccount(account));

        // Delete icon
        A deleteIcon = new A();
        deleteIcon.setIconSclass("z-icon-trash");
        deleteIcon.setSclass("action-icon delete-icon");
        deleteIcon.setTooltiptext("Delete Account");
        deleteIcon.addEventListener("onClick", e -> closeAccount(account));

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

    /**
     * Displays account details page.
     *
     * @param account selected account
     */
    private void viewAccount(Account account) {
        LOGGER.info("Viewing account {}", account.getAccountNumber());

        Executions.getCurrent().getSession().setAttribute(SESSION_SELECTED_ACCOUNT, account.getAccountNumber());
        loadCenter(VIEW_ACCOUNT_ZUL);
    }

    
    
    /**
     * Navigates to account edit page if account is active.
     *
     * @param account selected account
     */
    private void editAccount(Account account) {
        String status = account.getAccount_status().name();

        if (!"ACTIVE".equals(status)) {
            LOGGER.warn("Edit blocked for account {} with status {}",
                    account.getAccountNumber(), status);
            NotificationUtil.showInstant(
                    "warning",
                    "Your account is " + status + ".\nYou can't update account"
            );
            return;
        }

        LOGGER.info("Editing account {}", account.getAccountNumber());

        Executions.getCurrent().getSession().setAttribute(SESSION_SELECTED_ACCOUNT, account.getAccountNumber());
        loadCenter(UPDATE_ACCOUNT_ZUL);
    }

    
    
    /**
     * Navigates to account close page if account is active.
     *
     * @param account selected account
     */
    private void closeAccount(Account account) {
        String status = account.getAccount_status().name();

        if (!"ACTIVE".equals(status)) {
            LOGGER.warn("Close blocked for account {} with status {}",
                    account.getAccountNumber(), status);
            NotificationUtil.showInstant(
                    "warning",
                    "Your account is " + status + ". You can't delete account"
            );
            return;
        }

        LOGGER.info("Closing account {}", account.getAccountNumber());

        Executions.getCurrent().getSession().setAttribute(SESSION_SELECTED_ACCOUNT, account.getAccountNumber());
        loadCenter(CLOSE_ACCOUNT_ZUL);
    }
    
    
    
    /**
     * Loads a ZUL file into the center include.
     *
     * @param zulPath ZUL file path
     */
    private void loadCenter(String zulPath) {
        Include centerArea = (Include) getPage().getFellow("main_content_sec");
        centerArea.setSrc(zulPath);
    }

    
    
    /**
     * Handles back button navigation.
     */
    @Listen("onClick = #backBtn")
    public void onBackClick() {
        LOGGER.debug("Navigating back to customer dashboard");
        Executions.sendRedirect(CUSTOMER_DASHBOARD_ZUL);
    }
}
