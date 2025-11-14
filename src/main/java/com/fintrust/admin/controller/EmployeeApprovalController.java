package com.fintrust.admin.controller;


import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;

import com.fintrust.dao.AccountUpdateRequestDao;
import com.fintrust.model.AccountUpdateRequest;

import java.util.*;

public class EmployeeApprovalController extends SelectorComposer<Component> {

    @Wire private Listbox requestList;
    @Wire Button approveBtn,rejectBtn;
    private long currentEmployeeId = 201; // demo
    private AccountUpdateRequestDao dao = new AccountUpdateRequestDao();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
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
        	Messagebox.show("Please select one account first!" );
        	return;
        }
        AccountUpdateRequest req = requestList.getSelectedItem().getValue();
        dao.approveRequest(req.getRequestId(), currentEmployeeId);
        loadPendingRequests();
        Messagebox.show("Request approved successfully!");
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
        Messagebox.show("Request rejected.");
    }
}

