package com.fintrust.admin.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import com.fintrust.model.AccountCloseRequest;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.impl.AccountCloseRequestDao;

/**
 * Controller for approving or rejecting
 * account close requests by admin.
 */
public class AccountCloseApprovelComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(AccountCloseApprovelComposer.class);

	private final AccountCloseRequestDao accountCloseDao = new AccountCloseRequestDao();
	
	@Wire Listbox requestList;
	@Wire Button approveBtn;
	@Wire Button rejectBtn;
	@Wire Textbox searchBox; 
	    
	private Long currentEmployeeId;
	List<AccountCloseRequest> allCloseAccountRequest;
	private static final String WARNING = "warning";

	/**
	 * Called after UI components are composed.
	 *
	 * @param comp root UI component
	 * @throws Exception if initialization fails
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentEmployeeId = (Long) Sessions.getCurrent().getAttribute("admin_id");
		if (currentEmployeeId == null) {
			logger.warn("Admin session not found, access denied");
			return;
		}

		logger.info("Account close approval page loaded by adminId={}",currentEmployeeId);
		loadAccounts();
	}
	
	/**
	 * Loads all pending account close requests.
	 *
	 * @throws Exception if data loading fails
	 */
	private void loadAccounts() throws Exception {
		logger.debug("Loading pending account close requests");
		allCloseAccountRequest = accountCloseDao.getAllPendingRequest();
		loadPendingRequests(allCloseAccountRequest);
	}
	
	/**
	 * Displays pending requests in the listbox.
	 *
	 * @param allCloseAccountRequest list of pending requests
	 */
	private void loadPendingRequests(List<AccountCloseRequest> allCloseAccountRequest) {
		requestList.getItems().clear();
		if (allCloseAccountRequest.isEmpty()) {
			logger.info("No pending account close requests found");

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

		logger.debug("Rendering {} pending requests",allCloseAccountRequest.size());
		
		requestList.setModel(new ListModelList<>(allCloseAccountRequest));
	}
	
	/**
	 * Approves selected account close request.
	 *
	 * @throws Exception if approval fails
	 */
	@Listen("onClick=#approveBtn")
	public void approveRquest() throws Exception {
		if (requestList.getSelectedItem() == null) {
			logger.warn("Approve clicked without selecting request");
			NotificationUtil.showInstant(WARNING, "Please select one account first!");
			return;
		}

		AccountCloseRequest req = requestList.getSelectedItem().getValue();

		logger.info("Approving close requestId={} for accountNo={}",req.getRequestId(), req.getAccountNo());

		if (new AccountServiceImpl().checkBalance(req.getAccountNo()) > 0) {
			logger.warn("Account balance not zero for accountNo={}",req.getAccountNo());

			NotificationUtil.showInstant(WARNING,"First of all withdrawal your balance then do request for account closing");
			return;
		}

		accountCloseDao.approveRequest(req.getRequestId(),currentEmployeeId,"");

		logger.info("Account close request approved, requestId={}",req.getRequestId());

		NotificationUtil.showInstant("info", "Request approved successfully!");
		loadAccounts();
	}
	
	/**
	 * Rejects selected account close request.
	 *
	 * @throws Exception if rejection fails
	 */
	@Listen("onClick=#rejectBtn")
	public void rejectRquest() throws Exception {
		if (requestList.getSelectedItem() == null) {
			logger.warn("Reject clicked without selecting request");
			NotificationUtil.showInstant(WARNING, "Please select one account first!");
			return;
		}

		AccountCloseRequest req = requestList.getSelectedItem().getValue();

		logger.info("Rejecting close requestId={} for accountNo={}",req.getRequestId(), req.getAccountNo());

		accountCloseDao.rejectRequest(req.getRequestId(),currentEmployeeId,"");

		logger.info("Account close request rejected, requestId={}",req.getRequestId());

		NotificationUtil.showInstant("info", "Request rejected successfully!");
		loadAccounts();
	}
	
	/**
	 * Searches account close requests by account number.
	 *
	 * @throws Exception if search fails
	 */
	@Listen("onClick = #searchBtn")
	public void onSearch() throws Exception {

		String searchText = searchBox.getValue().trim().toLowerCase();
		logger.debug("Search triggered with value={}", searchText);

		if (searchText.isEmpty()) {
			loadPendingRequests(allCloseAccountRequest);
			return;
		}

		List<AccountCloseRequest> filtered = allCloseAccountRequest.stream()
		         .filter(acc -> (acc.getAccountNo() + "").contains(searchText))
				 .collect(Collectors.toList());

		logger.info("Search result count={}", filtered.size());

		loadPendingRequests(filtered);
	}
}
