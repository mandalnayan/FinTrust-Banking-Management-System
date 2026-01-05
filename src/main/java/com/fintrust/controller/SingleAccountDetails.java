package com.fintrust.controller;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.fintrust.dao.impl.BranchDao;
import com.fintrust.model.Account;
import com.fintrust.model.Branch;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.util.NotificationUtil;

/**
 * Controller class responsible for displaying
 * complete details of a single account.
 * @author Harish
 * @version 1.0
 */
public class SingleAccountDetails extends SelectorComposer<Window> {

    private static final long serialVersionUID = 667601436811702454L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleAccountDetails.class);

    /** Session key constant */
    private static final String SESSION_SELECTED_ACCOUNT_NO = "selected_account_no";

    /** Navigation constants */
    private static final String INDEX_PAGE = "index.zul";
    private static final String VIEW_ALL_ACCOUNT_ZUL = "/WEB-INF/components/view_all_account.zul";

    private final AccountServiceImpl accountService = new AccountServiceImpl();
    private final BranchDao branchDao = new BranchDao();

    /* UI Components */
    @Wire private Label accountNoLabel;
    @Wire private Label accountTypeLabel;
    @Wire private Label ifscCodeLabel;
    @Wire private Label accountBalanceLabel;
    @Wire private Label accountStatusLabel;
    @Wire private Label accountBranchLabel;
    @Wire private Label modeOfOperationLabel;
    @Wire private Label nomineeIdLabel;

    /**
     * Called after ZUL components are composed.
     * Loads account details from session and populates UI.
     *
     * @param comp the root window component
     * @throws Exception if unexpected error occurs
     */
    @Override
    public void doAfterCompose(final Window comp) throws Exception {
        super.doAfterCompose(comp);
        LOGGER.info("SingleAccountDetails controller initialized");

        Long selectedAccountNo = (Long) Executions.getCurrent().getSession().getAttribute(SESSION_SELECTED_ACCOUNT_NO);

        if (selectedAccountNo == null) {
            LOGGER.warn("Session expired or invalid access attempt");
            Executions.sendRedirect(INDEX_PAGE);
            return;
        }

        LOGGER.debug("Fetching account details for accountNo={}", selectedAccountNo);
        Account account = accountService.getAccountDetails(selectedAccountNo);

        if (account == null) {
            LOGGER.error("Account not found for accountNo={}", selectedAccountNo);
            NotificationUtil.showInstant("warning", "Account not found!");
            Executions.sendRedirect(INDEX_PAGE);
            return;
        }

        populateAccountDetails(account);
    }

    /**
     * Populates UI labels with account and branch details.
     *
     * @param account the account entity
     * @throws SQLException 
     */
    private void populateAccountDetails(final Account account) throws SQLException {
        LOGGER.debug("Populating account details on UI");

        accountNoLabel.setValue(String.valueOf(account.getAccountNumber()));
        accountTypeLabel.setValue(account.getAccountType().toString());
        accountBalanceLabel.setValue(String.valueOf(account.getBalance()));
        accountStatusLabel.setValue(account.getAccount_status().name());

        Branch branch = branchDao.findById(account.getBranchId());
        if (branch != null) {
            accountBranchLabel.setValue(branch.getBranchName());
            ifscCodeLabel.setValue(branch.getIfscCode());
            LOGGER.debug("Branch loaded for branchId={}", account.getBranchId());
        } else {
            LOGGER.warn("Branch not found for branchId={}", account.getBranchId());
        }

        modeOfOperationLabel.setValue("SELF");
        nomineeIdLabel.setValue(String.valueOf(account.getNominee_id()));
    }

    /**
     * Handles Back button click event and
     * navigates to the view-all-account page.
     */
    @Listen("onClick = #backBtn")
    public void onBackClick() {
        LOGGER.info("Back button clicked");

        Component root = getSelf();
        Include include = (Include) root.getPage().getFellow("main_content_sec");

        include.setSrc(VIEW_ALL_ACCOUNT_ZUL);
    }
}
