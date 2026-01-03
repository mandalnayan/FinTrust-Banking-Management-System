package com.fintrust.admin.controller;

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

public class ViewUpdateData extends SelectorComposer<Component>{
	 @Wire private Label requestId, accountNo, newAccountType, newBranchName, newModeOfOperation, status, requestedBy;
	 @Wire Button approveBtn,rejectBtn;
	 
	 private AccountUpdateRequestDao dao = new AccountUpdateRequestDao();
	 AccountUpdateRequest selected_request;
	 private Long currentEmployeeId;
	 
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentEmployeeId = (Long) Sessions.getCurrent().getAttribute("admin_user_id");
        if (currentEmployeeId == null) return;
		selected_request = (AccountUpdateRequest) Executions.getCurrent().getSession().getAttribute("selected_request");
     
        if (selected_request == null) {
        	NotificationUtil.showInstant("warning", "Request not found!");
            Executions.sendRedirect("index.zul");
            return;
        }

        requestId.setValue(selected_request.getRequestId()+"");
        accountNo.setValue(selected_request.getAccountNo()+"");
        newAccountType.setValue(selected_request.getNewAccountType()+"");
        newBranchName.setValue(selected_request.getNewBranchName()+"");
        newModeOfOperation.setValue(selected_request.getNewModeOfOperation());
        status.setValue(selected_request.getStatus()+"");
        
        requestedBy.setValue(selected_request.getRequestedBy().getFullName());
        
	}
	
	@Listen("onClick = #backBtn")
    public void onBackClick() {
      	Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");
		inc.setSrc("/admin/account/update_account_customer.zul");
    }
	
	@Listen("onClick = #approveBtn")
    public void approveRequest() throws Exception {
        dao.approveRequest(selected_request.getRequestId(), currentEmployeeId);
        NotificationUtil.showInstant("info", "Request approved successfully!");
        
        Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");
		inc.setSrc("/admin/account/update_account_customer.zul");
    }

    @Listen("onClick = #rejectBtn")
    public void rejectRequest() throws Exception {
        dao.rejectRequest(selected_request.getRequestId(), currentEmployeeId);
        NotificationUtil.showInstant("info", "Request rejected!");
        
        Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");
		inc.setSrc("/admin/account/update_account_customer.zul");
    }
}
