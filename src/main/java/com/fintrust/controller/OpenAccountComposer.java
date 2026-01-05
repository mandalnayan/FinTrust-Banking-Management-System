package com.fintrust.controller;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;

import com.fintrust.dao.impl.BranchDao;
import com.fintrust.model.Account;
import com.fintrust.model.Account.AccountType;
import com.fintrust.model.Branch;
import com.fintrust.model.Nominee;
import com.fintrust.model.Notification;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.NomineeServiceImp;
import com.fintrust.util.NotificationUtil;

/**
 * Controller responsible for handling
 * Open Account screen operations.
 * @author Harish
 * @version 1.0
 */
public class OpenAccountComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = -5397028081596145397L;
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAccountComposer.class);

    /** constants */
    private static final double MIN_INITIAL_DEPOSIT = 1000.0;
    private static final int NOMINEE_ID_LENGTH = 12;

    /* UI Components */
    @Wire Combobox accountTypeCombobox;
    @Wire Combobox branchCombobox;
    @Wire Combobox modeOfOperationCombobox;
    @Wire Combobox nomineeRelationCombobox;

    @Wire Longbox nomineeIdLongbox;
    @Wire Doublebox initialDepositDoublebox;
    @Wire Textbox nomineeNameTextbox;

    @Wire Button btnSubmit;
    @Wire Button btnReset;

    /* Services & DAO */
    private final AccountServiceImpl accountService = new AccountServiceImpl();
    private final NomineeServiceImp nomineeService = new NomineeServiceImp();
    private final BranchDao branchDao = new BranchDao();

    /**
     * Initializes UI components after ZUL composition.
     *
     * @param comp root component
     * @throws Exception if initialization fails
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        LOGGER.info("OpenAccountComposer initialized");

        loadAccountTypes();
        loadBranches();
        modeOfOperationCombobox.setSelectedIndex(0);
    }

    /**
     * Loads account types into combobox.
     */
    private void loadAccountTypes() {
        AccountType[] accountTypes = AccountType.values();
        for (AccountType type : accountTypes) {
            accountTypeCombobox.appendChild(new Comboitem(type.name()));
        }
        accountTypeCombobox.setSelectedIndex(0);
        LOGGER.debug("Account types loaded");
    }

    /**
     * Loads branch list into combobox.
     * @throws SQLException 
     */
    private void loadBranches() throws SQLException {
        List<Branch> branches = branchDao.findAll();
        for (Branch branch : branches) {
            branchCombobox.appendChild(new Comboitem(branch.getBranchName()));
        }
        LOGGER.debug("Branches loaded: {}", branches.size());
    }

    /**
     * Handles Account Submit button click.
     */
    @Listen("onClick = #btnAccountSubmit")
    public void onSubmit() {
        LOGGER.info("Submit button clicked");

        if (!isFormValid()) {
            LOGGER.warn("Form validation failed");
            return;
        }

        try {
            Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
            if (userId == null) {
                NotificationUtil.push("error", "Session expired. Please login again.");
                LOGGER.warn("User session expired");
                return;
            }

            String accountType = accountTypeCombobox.getSelectedItem().getLabel();
            String branchName = branchCombobox.getSelectedItem().getLabel();
            double deposit = initialDepositDoublebox.getValue();

            if (accountService.isAccountExists(userId, accountType)) {
                LOGGER.info("Account already exists for userId={}", userId);
                NotificationUtil.showInstant("warning", accountType + " Account already exists for this user.");
                resetForm();
                return;
            }

            long nomineeId = nomineeIdLongbox.getValue();
            Nominee nominee = new Nominee(nomineeId,nomineeNameTextbox.getValue().trim(),nomineeRelationCombobox.getValue().trim());

            Long nomineeDbId = nomineeService.isPresentNominee(nomineeId);
            alert(nomineeDbId + "");
            if (nomineeDbId == null) {
                nomineeDbId = nomineeService.saveNominee(nominee);
            }
            if (nomineeDbId == -1L) {
                LOGGER.error("Failed to save nominee");
                return;
            }

            long branchId = branchDao.findByBranchName(branchName).getBranchId();

            Account account = new Account();
            account.setAccountType(AccountType.valueOf(accountType));
            account.setBalance(deposit);
            account.setNominee_id(nomineeDbId);
            account.setBranchId(branchId);

            Notification notification = accountService.openAccount(account);
            NotificationUtil.push(notification);

            if ("info".equals(notification.getType())) {
                LOGGER.info("Account created successfully");
                resetForm();               
            }

        } catch (IllegalArgumentException ex) {
            LOGGER.error("Invalid input", ex);
            NotificationUtil.push("error", "Invalid input provided.");
        } catch (Exception ex) {
            LOGGER.error("Account creation failed", ex);
            NotificationUtil.push("error", "Server error. Failed to create account.");
        }
        Executions.sendRedirect("");
    }

    /**
     * Handles Reset button click.
     */
    @Listen("onClick = #btnAccountReset")
    public void onReset() {
        LOGGER.info("Reset button clicked");
        resetForm();
    }

    /**
     * Validates all form fields.
     *
     * @return true if form is valid
     */
    private boolean isFormValid() {

        if (accountTypeCombobox.getSelectedItem() == null) {
            showWarning("Please select Account Type.");
            return false;
        }
        if (branchCombobox.getSelectedItem() == null) {
            showWarning("Please select Branch.");
            return false;
        }
        if (initialDepositDoublebox.getValue() == null
                || initialDepositDoublebox.getValue() < MIN_INITIAL_DEPOSIT) {
            showWarning("Minimum deposit must be ₹1000 or more.");
            return false;
        }
        if (modeOfOperationCombobox.getSelectedItem() == null) {
            showWarning("Please select Mode of Operation.");
            return false;
        }
        if (nomineeNameTextbox.getValue().trim().isEmpty()
                || !nomineeNameTextbox.getValue().matches("[a-zA-Z ]+")) {
            showWarning("Invalid Nominee Name.");
            return false;
        }
        if (nomineeRelationCombobox.getSelectedItem() == null) {
            showWarning("Please select Nominee Relation.");
            return false;
        }
        if (nomineeIdLongbox.getValue() == null
                || String.valueOf(nomineeIdLongbox.getValue()).length() != NOMINEE_ID_LENGTH) {
            showWarning("Nominee Id must be 12 digits.");
            return false;
        }
        return true;
    }

    /**
     * Displays warning message.
     *
     * @param message warning text
     */
    private void showWarning(String message) {
        NotificationUtil.showInstant("warning", message);
    }

    /**
     * Resets all form fields.
     */
    private void resetForm() {
        accountTypeCombobox.setSelectedIndex(-1);
        branchCombobox.setSelectedIndex(-1);
        modeOfOperationCombobox.setSelectedIndex(-1);
        initialDepositDoublebox.setValue(null);
        nomineeNameTextbox.setValue("");
        nomineeIdLongbox.setText("");
        nomineeRelationCombobox.setValue("");
        LOGGER.debug("Form reset completed");
    }
}
