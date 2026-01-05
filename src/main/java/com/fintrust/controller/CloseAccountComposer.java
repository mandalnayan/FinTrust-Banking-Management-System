package com.fintrust.controller;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.fintrust.model.AccountCloseRequest;
import com.fintrust.service.AccountCloseRequestService;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

/**
 * Controller responsible for submitting account close requests.
 * Handles user confirmation, validation, and request submission.
 *
 * @author Harish
 * @version 1.0
 */
public class CloseAccountComposer extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseAccountComposer.class);

    /* ---------- Constants ---------- */
    private static final String SESSION_SELECTED_ACCOUNT = "selected_account_no";
    private static final String SESSION_USER_ID = "user_id";
    private static final String MAIN_CONTENT = "main_content_sec";
    private static final String VIEW_ALL_ACCOUNTS_PAGE = "/WEB-INF/components/view_all_account.zul";

    /* ---------- UI Components ---------- */
    @Wire private Label accountNoLabel;
    @Wire private Textbox reasonTextbox;
    @Wire private Checkbox confirmCloseCheckbox;

    /* ---------- Services ---------- */
    private final AccountCloseRequestService closeRequestService = new AccountCloseRequestService();
    private final UserServiceImpl userService = new UserServiceImpl();

    private Long accountNumber;

    /**
     * Initializes account number after UI composition.
     *
     * @param comp root UI component
     * @throws Exception if session data is missing
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

        LOGGER.info("Preparing close request for accountNumber={}", accountNumber);
        accountNoLabel.setValue(String.valueOf(accountNumber));
    }

    /**
     * Submits account close request after validation and confirmation.
     *
     * @throws SQLException if database access fails
     */
    @Listen("onClick=#btnSubmit")
    public void submitCloseAccountRequest() throws SQLException {

        if (!confirmCloseCheckbox.isChecked()) {
            LOGGER.debug("Close request confirmation checkbox not selected");
            NotificationUtil.showInstant("warning", "Please confirm first!");
            return;
        }

        Long userId = (Long) Sessions.getCurrent().getAttribute(SESSION_USER_ID);

        if (userId == null) {
            LOGGER.warn("User session expired during close request");
            redirectToAccountList();
            return;
        }

        AccountCloseRequest request = buildCloseRequest(userId);

        LOGGER.info("Submitting close request for accountNumber={}", accountNumber);

        boolean isSaved = closeRequestService.saveReq(request);

        if (isSaved) {
            NotificationUtil.showInstant(
                    "info",
                    "Request sent successfully for closing the account"
            );
            LOGGER.info("Close request submitted successfully");
        } else {
            NotificationUtil.showInstant(
                    "warning",
                    "Request already submitted for closing the account"
            );
            LOGGER.warn("Duplicate close request attempt detected");
        }

        redirectToAccountList();
    }

    /**
     * Resets close request process and navigates back to account list.
     */
    @Listen("onClick=#btnReset")
    public void resetRequest() {
        LOGGER.debug("Close request reset by user");
        redirectToAccountList();
    }

    /**
     * Builds account close request object.
     *
     * @param userId logged-in user ID
     * @return populated AccountCloseRequest object
     * @throws SQLException 
     */
    private AccountCloseRequest buildCloseRequest(Long userId) throws SQLException {
        AccountCloseRequest request = new AccountCloseRequest();
        request.setAccountNo(accountNumber);
        request.setReason(reasonTextbox.getValue());
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
}
