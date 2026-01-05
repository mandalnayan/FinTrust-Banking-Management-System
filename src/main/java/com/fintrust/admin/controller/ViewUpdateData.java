package com.fintrust.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

import com.fintrust.dao.impl.AccountUpdateRequestDao;
import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.util.NotificationUtil;

/**
 * Controller to display and process
 * a single account update request.
 *
 * Follows secure coding practices:
 * - Session validation
 * - Minimal logging
 * - No sensitive data exposure
 */
public class ViewUpdateData extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1L;

    /* -------------------- Constants -------------------- */

    private static final String SESSION_ADMIN_ID = "admin_id";
    private static final String SESSION_SELECTED_REQUEST = "selected_request";

    private static final String MAIN_CONTENT = "main_content_sec";
    private static final String UPDATE_LIST_PAGE = "/admin/account/update_account_customer.zul";
    private static final String INDEX_PAGE = "index.zul";

    private static final String INFO = "info";
    private static final String WARNING = "warning";

    /* -------------------- Logger -------------------- */

    private static final Logger logger =
            LoggerFactory.getLogger(ViewUpdateData.class);

    /* -------------------- UI Components -------------------- */

    @Wire private Label requestId;
    @Wire private Label accountNo;
    @Wire private Label newAccountType;
    @Wire private Label newBranchName;
    @Wire private Label newModeOfOperation;
    @Wire private Label status;
    @Wire private Label requestedBy;

    @Wire private Button approveBtn;
    @Wire private Button rejectBtn;


    private final AccountUpdateRequestDao dao = new AccountUpdateRequestDao();

    private AccountUpdateRequest selectedRequest;
    private Long currentEmployeeId;

    /**
     * Initializes request details after UI composition.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        currentEmployeeId = (Long) Sessions.getCurrent().getAttribute(SESSION_ADMIN_ID);
        if (currentEmployeeId == null) {
            logger.warn("Unauthorized access attempt to update request view");
            return;
        }

        selectedRequest =(AccountUpdateRequest) Executions.getCurrent().getSession().getAttribute(SESSION_SELECTED_REQUEST);

        if (selectedRequest == null) {
            logger.warn("Account update request missing in session");
            NotificationUtil.showInstant(WARNING, "Request not found!");
            Executions.sendRedirect(INDEX_PAGE);
            return;
        }

        logger.info("Viewing account update requestId={}",selectedRequest.getRequestId());
        populateRequestDetails();
    }

    /**
     * Populates UI labels with request data.
     */
    private void populateRequestDetails() {
        requestId.setValue(String.valueOf(selectedRequest.getRequestId()));
        accountNo.setValue(String.valueOf(selectedRequest.getAccountNo()));
        newAccountType.setValue(selectedRequest.getNewAccountType());
        newBranchName.setValue(selectedRequest.getNewBranchName());
        newModeOfOperation.setValue(selectedRequest.getNewModeOfOperation());
        status.setValue(selectedRequest.getStatus().toString());
        requestedBy.setValue(selectedRequest.getRequestedBy().getFullName());
    }

    /**
     * Navigates back to update request list.
     */
    @Listen("onClick = #backBtn")
    public void onBackClick() {
        redirectToListPage();
    }

    /**
     * Approves the selected update request.
     */
    @Listen("onClick = #approveBtn")
    public void approveRequest() throws Exception {

        logger.info("Approving requestId={} by adminId={}",selectedRequest.getRequestId(),currentEmployeeId);

        dao.approveRequest(selectedRequest.getRequestId(),currentEmployeeId);

        NotificationUtil.showInstant(INFO, "Request approved successfully!");
        redirectToListPage();
    }

    /**
     * Rejects the selected update request.
     */
    @Listen("onClick = #rejectBtn")
    public void rejectRequest() throws Exception {

        logger.info("Rejecting requestId={} by adminId={}",selectedRequest.getRequestId(),currentEmployeeId);

        dao.rejectRequest(selectedRequest.getRequestId(),currentEmployeeId);

        NotificationUtil.showInstant(INFO, "Request rejected!");
        redirectToListPage();
    }

    /**
     * Redirects to update request list page.
     */
    private void redirectToListPage() {
        Component root = getSelf();
        Include inc = (Include) root.getPage().getFellow(MAIN_CONTENT);
        inc.setSrc(UPDATE_LIST_PAGE);
    }
}
