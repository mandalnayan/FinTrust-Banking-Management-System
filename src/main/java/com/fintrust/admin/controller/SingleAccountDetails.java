package com.fintrust.admin.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import com.fintrust.dao.impl.BranchDao;
import com.fintrust.model.Account;
import com.fintrust.model.Branch;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.util.NotificationUtil;


public class SingleAccountDetails extends SelectorComposer<Window> {
	private final AccountServiceImpl acconntService = new AccountServiceImpl();

    @Wire private Label accountNo, accountType, ifscCode, accountBalance, accountStatus, accountBranch, modeOfOperation, nomineeId;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        Long selectedAccountNo = (Long) Executions.getCurrent().getSession().getAttribute("selected_account_no");
        //Long customerId = (Long) Executions.getCurrent().getSession().getAttribute("customer_id");
   
       // if (selectedAccountNo == null || customerId == null) {
         if (selectedAccountNo == null) {
            Messagebox.show("Invalid access or session expired!", "Error", Messagebox.OK, Messagebox.ERROR);
           
            Executions.sendRedirect("index.zul");
            return;
        }

        Account acc = acconntService.getAccountDetails(selectedAccountNo);
        if (acc == null) {
        	NotificationUtil.showInstant("warning", "Account not found!");
            Executions.sendRedirect("index.zul");
            return;
        }

        accountNo.setValue(acc.getAccountNumber()+"");
        accountType.setValue(acc.getAccountType().toString());
        accountBalance.setValue(acc.getBalance()+"");
        accountStatus.setValue(acc.getAccount_status().name());

        Branch branch = new BranchDao().findById(acc.getBranchId());
        
        if (branch != null) {
        	accountBranch.setValue(branch.getBranchName());
        	ifscCode.setValue(branch.getIfscCode());
 
        }
        
        modeOfOperation.setValue("self");
        nomineeId.setValue(acc.getNominee_id()+"");
    }

    @Listen("onClick = #backBtn")
    public void onBackClick() {
        //Executions.sendRedirect("/user/userDashboard.zul");
      	Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");
		inc.setSrc("/admin/account/view_all_account.zul");
    }
}