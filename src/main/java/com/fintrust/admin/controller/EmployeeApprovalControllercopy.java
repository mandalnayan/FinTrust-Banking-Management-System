package com.fintrust.admin.controller;


import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.impl.AccountUpdateRequestDao;

import com.fintrust.dao.impl.AccountUpdateRequestDao;


import java.util.*;
import java.util.stream.Collectors;

public class EmployeeApprovalControllercopy extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;
    
	@Wire private Listbox requestList;
    @Wire Button approveBtn,rejectBtn;
    private Long currentEmployeeId;
    
    @Wire
	private Textbox searchBox; 
    
    List<AccountUpdateRequest> allUpdateAccountRequest;
    private AccountUpdateRequestDao dao = new AccountUpdateRequestDao();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        currentEmployeeId = (Long) Sessions.getCurrent().getAttribute("admin_id");
        if (currentEmployeeId == null) return;
        loadAccounts();
    }
    
    /** Load all accounts initially 
     * @throws Exception **/
    private void loadAccounts() throws Exception {
    	allUpdateAccountRequest = dao.getPendingRequests();
        loadPendingRequests(allUpdateAccountRequest);
    }

    private void loadPendingRequests(List<AccountUpdateRequest> allUpdateAccountRequest) throws Exception {
        if(allUpdateAccountRequest.isEmpty()) {
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
        requestList.setModel(new ListModelList<>(allUpdateAccountRequest));
    }
    
    @Listen("onSelect = #requestList")
    public void onRowSelect() {
        Listitem selectedItem = requestList.getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        // This is your full object
        AccountUpdateRequest req = (AccountUpdateRequest) selectedItem.getValue();
        Executions.getCurrent().getSession().setAttribute("selected_request", req);
        
        Include centerArea = (Include) getPage().getFellow("main_content_sec");
        centerArea.setSrc("/admin/account/view_Upated_Data.zul");
    }
    
    /** Search event 
     * @throws Exception **/
    @Listen("onClick = #searchBtn")
    public void onSearch() throws Exception {
        String searchText = searchBox.getValue().trim().toLowerCase();

        if (searchText.isEmpty()) {
        	loadPendingRequests(allUpdateAccountRequest);
            return;
        }

        List<com.fintrust.model.AccountUpdateRequest> filtered = allUpdateAccountRequest.stream()
                .filter(acc -> (acc.getAccountNo() +"").contains(searchText))
                .collect(Collectors.toList());

        loadPendingRequests(filtered);
    }
}

