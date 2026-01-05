package com.fintrust.admin.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.fintrust.dao.impl.AccountUpdateRequestDao;
import com.fintrust.model.AccountUpdateRequest;

/**
 * Controller for approving or reviewing account update requests.
 */
public class AccountUpdateApprovalController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    /* ---------- constants ---------- */
    private static final String SESSION_ADMIN_ID = "admin_id";
    private static final String SESSION_SELECTED_REQUEST = "selected_request";
    private static final String CENTER_SECTION_ID = "main_content_sec";
    private static final String VIEW_UPDATE_ZUL = "/admin/account/view_Upated_Data.zul";

    /* ---------- logger ---------- */
    private static final Logger log = LoggerFactory.getLogger(AccountUpdateApprovalController.class);

    /* ---------- UI components ---------- */
    @Wire Listbox requestList;
    @Wire Button approveBtn, rejectBtn;
    @Wire Textbox searchBox;
    @Wire Hbox messageBox;


    private Long currentEmployeeId;
    List<AccountUpdateRequest> allUpdateAccountRequest;

    private AccountUpdateRequestDao dao = new AccountUpdateRequestDao();

    /**
     * Lifecycle method invoked after ZUL components are composed.
     * Loads pending requests only if admin session exists.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        currentEmployeeId = (Long) Sessions.getCurrent().getAttribute(SESSION_ADMIN_ID);

        if (currentEmployeeId == null) {
            log.warn("Admin session not found. Account update approval screen not initialized.");
            return;
        }

        log.info("Account update approval screen loaded for adminId={}", currentEmployeeId);
        loadAccounts();
    }

    
    /**
     * Loads all pending account update requests from database.
     */
    private void loadAccounts() throws Exception {
        allUpdateAccountRequest = dao.getPendingRequests();
        loadPendingRequests(allUpdateAccountRequest);
    }

    
    /**
     * Populates the Listbox with pending account update requests.
     *
     * @param allUpdateAccountRequest list of pending requests
     */
    private void loadPendingRequests(List<AccountUpdateRequest> allUpdateAccountRequest) throws Exception {
        requestList.getItems().clear();

        if (allUpdateAccountRequest.isEmpty()) {
            messageBox.setVisible(true);
            return;
        }

        messageBox.setVisible(false);
        requestList.setModel(new ListModelList<>(allUpdateAccountRequest));
    }

    
    /**
     * Triggered when an admin selects a request from the list.
     * Stores the selected request in session and navigates
     * to the detailed review screen.
     */
    @Listen("onSelect = #requestList")
    public void onRowSelect() {
        Listitem selectedItem = requestList.getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        AccountUpdateRequest req = (AccountUpdateRequest) selectedItem.getValue();

        Executions.getCurrent().getSession().setAttribute(SESSION_SELECTED_REQUEST, req);

        log.debug("Account update request selected. requestId={}", req.getRequestId());

        Include centerArea = (Include) getPage().getFellow(CENTER_SECTION_ID);
        centerArea.setSrc(VIEW_UPDATE_ZUL);
    }

    /**
     * Handles search action for account update requests.
     * Filters requests by account number.
     */
    @Listen("onClick = #searchBtn")
    public void onSearch() throws Exception {
        String searchText = searchBox.getValue().trim();

        if (searchText.isEmpty()) {
            loadPendingRequests(allUpdateAccountRequest);
            return;
        }

        List<AccountUpdateRequest> filtered = allUpdateAccountRequest
        		                .stream()
                                .filter(acc -> String.valueOf(acc.getAccountNo()).contains(searchText))
                                .collect(Collectors.toList());

        log.debug("Search executed for accountNo keyword='{}'. ResultCount={}",searchText, filtered.size());
        loadPendingRequests(filtered);
    }
}
