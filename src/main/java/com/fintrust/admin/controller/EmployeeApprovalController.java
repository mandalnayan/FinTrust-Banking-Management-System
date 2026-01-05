package com.fintrust.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.*;

import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.impl.AccountUpdateRequestDao;

import java.util.List;

/**
 * Controller for employee/admin approval of
 * account update requests.
 */
public class EmployeeApprovalController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeApprovalController.class);
    
	@Wire private Listbox requestList;
    @Wire Button approveBtn;
    @Wire Button rejectBtn;
    
    private Long currentEmployeeId;
    private AccountUpdateRequestDao dao = new AccountUpdateRequestDao();

    /**
     * Initializes the approval page after component creation.
     *
     * @param comp root UI component
     * @throws Exception if initialization fails
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        currentEmployeeId = (Long) Sessions.getCurrent().getAttribute("admin_user_id");

        if (currentEmployeeId == null) {
            logger.warn("Admin session not found, approval page access denied");
            return;
        }

        logger.info("Employee approval page loaded by adminId={}",currentEmployeeId);
        loadPendingRequests();
    }

    /**
     * Loads all pending account update requests.
     *
     * @throws Exception if data loading fails
     */
    private void loadPendingRequests() throws Exception {

        List<AccountUpdateRequest> list = dao.getPendingRequests();

        if (list.isEmpty()) {
            logger.info("No pending account update requests found");

        	approveBtn.setVisible(false);
        	rejectBtn.setVisible(false);
        	
        	Hbox box = new Hbox();
        	box.setWidth("100%");
        	box.setHeight("50px");
        	box.setPack("center");
        	box.setAlign("center");
        	
        	Label label = new Label("No Account Pending for the Approvel");
        	label.setStyle("font-size:22px");
        	box.appendChild(label);
        	
        	requestList.getParent().appendChild(box);
        	return;
        }

        logger.debug("Loaded {} pending update requests", list.size());

        requestList.setModel(new ListModelList<>(list));
    }

    /**
     * Approves the selected account update request.
     *
     * @throws Exception if approval fails
     */
    @Listen("onClick = #approveBtn")
    public void approveRequest() throws Exception {

    	if (requestList.getSelectedItem() == null) {
            logger.warn("Approve clicked without selecting any request");
        	NotificationUtil.showInstant("warning", "Please select one account first!");
        	return;
        }

        AccountUpdateRequest req = requestList.getSelectedItem().getValue();
        
        logger.info("Approving update requestId={}",req.getRequestId());
        
        dao.approveRequest(req.getRequestId(), currentEmployeeId);
        loadPendingRequests();
        
        NotificationUtil.showInstant("info", "Request approved successfully!");
    }

    /**
     * Rejects the selected account update request.
     *
     * @throws Exception if rejection fails
     */
    @Listen("onClick = #rejectBtn")
    public void rejectRequest() throws Exception {

    	if (requestList.getSelectedItem() == null) {
            logger.warn("Reject clicked without selecting any request");
        	Messagebox.show("Please select one account first!");
        	return;
        }

        AccountUpdateRequest req = requestList.getSelectedItem().getValue();

        logger.info("Rejecting update requestId={}",req.getRequestId());

        dao.rejectRequest(req.getRequestId(), currentEmployeeId);
        loadPendingRequests();

        NotificationUtil.showInstant("info", "Request rejected!");
    }
}
