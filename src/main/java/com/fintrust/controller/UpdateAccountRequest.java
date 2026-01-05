package com.fintrust.controller;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

import com.fintrust.model.Account;
import com.fintrust.model.Account.AccountType;
import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.model.Branch;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.RequestUpdateService;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.impl.BranchDao;

/**
 * Controller responsible for submitting account update requests.
 * Allows user to request changes in account type, branch,
 * and mode of operation.
 *
 * @author Harish
 * @version 1.0
 */
public class UpdateAccountRequest extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAccountRequest.class);

    /* ---------- Session & Page Constants ---------- */
    private static final String SESSION_SELECTED_ACCOUNT = "selected_account_no";
    private static final String SESSION_USER_ID = "user_id";
    private static final String MAIN_CONTENT = "main_content_sec";
    private static final String VIEW_ALL_ACCOUNTS_PAGE = "/WEB-INF/components/view_all_account.zul";

    /* ---------- UI Components ---------- */
    @Wire private Label accountNoLabel;
    @Wire private Label accountBalanceLabel;
    @Wire private Label accountStatusLabel;

    @Wire private Combobox accountTypeComboBox;
    @Wire private Combobox accountBranchBox;
    @Wire private Combobox accountModeBox;

    /* ---------- Services & DAOs ---------- */
    private final AccountServiceImpl accountService = new AccountServiceImpl();
    private final BranchDao branchDao = new BranchDao();
    private final RequestUpdateService requestUpdateService = new RequestUpdateService();
    private final UserServiceImpl userService = new UserServiceImpl();

    private Long accountNumber;

    /**
     * Initializes account details and dropdown values after UI composition.
     *
     * @param comp the root UI component
     * @throws Exception if account data loading fails
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        accountNumber = (Long) Executions.getCurrent().getSession().getAttribute(SESSION_SELECTED_ACCOUNT);

        if (accountNumber == null) {
            LOGGER.warn("No account number found in session");
            redirectToAccountList();
            return;
        }

        LOGGER.info("Loading account details for accountNumber={}", accountNumber);
        Account account = accountService.getAccountDetails(accountNumber);

        if (account == null) {
            LOGGER.error("Account not found for accountNumber={}", accountNumber);
            redirectToAccountList();
            return;
        }

        populateAccountDetails(account);
        populateAccountTypeCombo(account);
        populateBranchCombo(account);
    }

    /**
     * Submits account update request.
     *
     * @throws SQLException if database access fails
     */
    @Listen("onClick=#update")
    public void sendUpdateAccountRequest() throws SQLException {

        if (!isFormValid()) {
            LOGGER.debug("Update request validation failed");
            return;
        }

        AccountUpdateRequest request = buildUpdateRequest();
        LOGGER.info("Submitting update request for accountNumber={}", accountNumber);
        boolean isSubmitted = requestUpdateService.updateRequest(request);

        if (isSubmitted) {
            NotificationUtil.showInstant(
                    "info",
                    "Request submitted successfully.\nYour request will be processed shortly."
            );
        } else {
            NotificationUtil.showInstant(
                    "warning",
                    "Request already exists. Please check request status."
            );
        }

        redirectToAccountList();
    }

    /**
     * Cancels update request and navigates back to account list.
     */
    @Listen("onClick=#cancel")
    public void cancelUpdateAccountRequest() {
        LOGGER.debug("Account update request cancelled by user");
        redirectToAccountList();
    }
    

    /**
     * Validates update request form fields.
     *
     * @return true if form is valid, false otherwise
     */
    
    public boolean isFormValid() {
        if (accountTypeComboBox.getSelectedItem() == null) {
            showWarning("Please select Account Type.");
            return false;
        }

        if (accountBranchBox.getSelectedItem() == null) {
            showWarning("Please select Branch.");
            return false;
        }

        if (accountModeBox.getSelectedItem() == null) {
            showWarning("Please select Mode of Operation.");
            return false;
        }
        return true;
    }

    /* ================= Helper Methods ================= */
    
    /**
     * Populates account details labels.
     *
     * @param account account entity
     */
    private void populateAccountDetails(Account account) {
        accountNoLabel.setValue(String.valueOf(account.getAccountNumber()));
        accountBalanceLabel.setValue(String.valueOf(account.getBalance()));
        accountStatusLabel.setValue(account.getAccount_status().name());
    }

    /**
     * Populates account type dropdown excluding current type.
     *
     * @param account account entity
     */
    private void populateAccountTypeCombo(Account account) {
        for (AccountType accType : AccountType.values()) {
            if (accType != account.getAccountType()) {
                Comboitem item = new Comboitem(accType.name());
                item.setValue(accType.name());
                accountTypeComboBox.appendChild(item);
            }
        }
    }

    /**
     * Populates branch dropdown excluding current branch.
     *
     * @param account account entity
     * @throws SQLException 
     */
    private void populateBranchCombo(Account account) throws SQLException {
        List<Branch> branches = branchDao.findAll();
        for (Branch branch : branches) {
            if (!branch.getBranchId().equals(account.getBranchId())) {
                Comboitem item = new Comboitem(branch.getBranchName());
                item.setValue(branch.getBranchName());
                accountBranchBox.appendChild(item);
            }
        }
    }

    /**
     * Builds account update request object.
     *
     * @return populated AccountUpdateRequest
     * @throws SQLException 
     */
    private AccountUpdateRequest buildUpdateRequest() throws SQLException {

        Long userId = (Long) Executions.getCurrent().getSession().getAttribute(SESSION_USER_ID);

        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setAccountNo(accountNumber);
        request.setNewAccountType(accountTypeComboBox.getSelectedItem().getValue());
        request.setNewBranchName(accountBranchBox.getSelectedItem().getValue());
        request.setNewModeOfOperation(accountModeBox.getSelectedItem().getValue());
        request.setRequestedBy(userService.getUserByUserId(userId));
        return request;
    }

    /**
     * Redirects user to account list page.
     */
    private void redirectToAccountList() {
        Include include = (Include) getSelf().getPage().getFellow(MAIN_CONTENT);
        include.setSrc(VIEW_ALL_ACCOUNTS_PAGE);
    }

    /**
     * Displays warning notification.
     *
     * @param message - warning message
     */
    private void showWarning(String message) {
        NotificationUtil.showInstant("warning", message);
    }
}
