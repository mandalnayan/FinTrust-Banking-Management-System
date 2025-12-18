package com.fintrust.admin.controller;


import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.*;

import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.impl.AccountUpdateRequestDao;

import com.fintrust.dao.impl.AccountUpdateRequestDao;


import java.util.*;

public class EmployeeApprovalController extends SelectorComposer<Component> {

    @Wire private Listbox requestList;
    @Wire Button approveBtn,rejectBtn;
    private Long currentEmployeeId;
    
    private AccountUpdateRequestDao dao = new AccountUpdateRequestDao();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        currentEmployeeId = (Long) Sessions.getCurrent().getAttribute("admin_user_id");
        if (currentEmployeeId == null) return;
        loadPendingRequests();
    }

    private void loadPendingRequests() throws Exception {
        List<AccountUpdateRequest> list = dao.getPendingRequests();
        if(list.size()==0) {
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

    @Listen("onClick = #approveBtn")
    public void approveRequest() throws Exception {
    	if(requestList.getSelectedItem()==null) {        	
        	 NotificationUtil.showInstant("warning", "Please select one account first!");
        	return;
        }
        AccountUpdateRequest req = requestList.getSelectedItem().getValue();
        dao.approveRequest(req.getRequestId(), currentEmployeeId);
        loadPendingRequests();
        NotificationUtil.showInstant("info", "Request approved successfully!");
      
    }

    @Listen("onClick = #rejectBtn")
    public void rejectRequest() throws Exception {
    	if(requestList.getSelectedItem()==null) {
        	Messagebox.show("Please select one account first!" );
        	return;
        }
        AccountUpdateRequest req = requestList.getSelectedItem().getValue();
        dao.rejectRequest(req.getRequestId(), currentEmployeeId);
        loadPendingRequests();
        NotificationUtil.showInstant("info", "Request rejected!");
    }
}

