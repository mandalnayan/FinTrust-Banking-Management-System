package com.fintrust.admin.controller;

import java.util.List;

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
import org.zkoss.zul.Messagebox;

import com.fintrust.model.AccountCloseRequest;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.EmailService;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.impl.AccountCloseRequestDao;

public class AccountCloseApprovelComposer extends SelectorComposer<Component>{
	private final AccountCloseRequestDao accountCloseDao = new AccountCloseRequestDao();
	
	@Wire Listbox requestList;
	@Wire Button approveBtn, rejectBtn;
	    
	private Long currentEmployeeId;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentEmployeeId = (Long) Sessions.getCurrent().getAttribute("admin_user_id");
		if (currentEmployeeId == null) return;
		loadPendingRequests();
	}
	
	private void loadPendingRequests() {
		requestList.getItems().clear();
		List<AccountCloseRequest> list = accountCloseDao.getAllPendingRequest();
	
		if(list.size() == 0) {
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
		 requestList.setModel(new ListModelList<>(list));
	}
	
	@Listen("onClick=#approveBtn")
	public void approveRquest() throws Exception {
		if(requestList.getSelectedItem()==null) {
			NotificationUtil.showInstant("warning", "Please select one account first!");

        	return;
        }
		AccountCloseRequest req = requestList.getSelectedItem().getValue();
		
		if(new AccountServiceImpl().checkBalance(req.getAccountNo()) > 0) {
			NotificationUtil.showInstant("warning", "First of all withdrawal your balance then do request for account closing");
		    return;
		}
		
        accountCloseDao.approveRequest(req.getRequestId(), currentEmployeeId, "");
        loadPendingRequests();
	}
	
	@Listen("onClick=#rejectBtn")
	public void rejectRquest() throws Exception {
		if(requestList.getSelectedItem()==null) {
			NotificationUtil.showInstant("warning", "Please select one account first!");
        	return;
        }
		AccountCloseRequest req = requestList.getSelectedItem().getValue();
        accountCloseDao.rejectRequest(req.getRequestId(), currentEmployeeId, "");
        
        loadPendingRequests();
	}
}
