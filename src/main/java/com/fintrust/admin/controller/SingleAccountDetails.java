package com.fintrust.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.fintrust.dao.impl.BranchDao;
import com.fintrust.model.Account;
import com.fintrust.model.Branch;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.util.NotificationUtil;

/**
 * Controller for displaying details of a single account.
 * 
 * Account number is taken from session and used to fetch
 * account and branch details.
 */
public class SingleAccountDetails extends SelectorComposer<Window> {

    private static final long serialVersionUID = -9105198266913683093L;
	private static final Logger logger = LoggerFactory.getLogger(SingleAccountDetails.class);

	private final AccountServiceImpl acconntService = new AccountServiceImpl();

    @Wire private Label accountNo;
    @Wire private Label accountType;
    @Wire private Label ifscCode;
    @Wire private Label accountBalance;
    @Wire private Label accountStatus;
    @Wire private Label accountBranch;
    @Wire private Label modeOfOperation;
    @Wire private Label nomineeId;

    /**
     * Initializes account detail page after component composition.
     *
     * @param comp main window component
     * @throws Exception if initialization fails
     */
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        Long selectedAccountNo = (Long) Executions.getCurrent().getSession().getAttribute("selected_account_no");

        if (selectedAccountNo == null) {
            logger.warn("Session expired or invalid access to account details page");
            Executions.sendRedirect("index.zul");
            return;
        }

        Account acc = acconntService.getAccountDetails(selectedAccountNo);

        if (acc == null) {
            logger.warn("Account not found for accountNo={}", selectedAccountNo);

        	NotificationUtil.showInstant("warning", "Account not found!");
            Executions.sendRedirect("index.zul");
            return;
        }

        logger.info("Displaying details for accountNo={}",selectedAccountNo);

        accountNo.setValue(acc.getAccountNumber() + "");
        accountType.setValue(acc.getAccountType().toString());
        accountBalance.setValue(acc.getBalance() + "");
        accountStatus.setValue(acc.getAccount_status().name());

        Branch branch = new BranchDao().findById(acc.getBranchId());

        if (branch != null) {
        	accountBranch.setValue(branch.getBranchName());
        	ifscCode.setValue(branch.getIfscCode());
        }

        modeOfOperation.setValue("self");
        nomineeId.setValue(acc.getNominee_id() + "");
    }

    /**
     * Handles back button click and navigates
     * to all accounts view.
     */
    @Listen("onClick = #backBtn")
    public void onBackClick() {

        logger.debug("Navigating back to all account list");

      	Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");

		inc.setSrc("/admin/account/view_all_account.zul");
    }
}
