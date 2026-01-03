package com.fintrust.controller;

import java.sql.SQLException;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import com.fintrust.model.Account;
import com.fintrust.model.Account.AccountType;
import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.model.Branch;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.RequestUpdateService;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;
import com.fasterxml.jackson.databind.introspect.AccessorNamingStrategy;
import com.fintrust.dao.impl.AccountUpdateRequestDao;
import com.fintrust.dao.impl.BranchDao;

public class UpdateAccountRequest extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;

	@Wire
	private Label accountNo, accountBalance, accountStatus;
	@Wire
	private Combobox accountTypeComboBox, accountBranchBox, accountModeBox;

	private final AccountServiceImpl acconntService = new AccountServiceImpl();
	private final BranchDao branchDao = new BranchDao();
	private Long accountNum;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		accountNum = (Long) Executions.getCurrent().getSession().getAttribute("selected_account_no");

		Account acc = acconntService.getAccountDetails(accountNum);
		accountNo.setValue(acc.getAccountNumber() + "");
		accountBalance.setValue(acc.getBalance() + "");
		accountStatus.setValue(acc.getAccount_status().name());

		// set the account type in combobox except current account type
		AccountType[] allAccountType = AccountType.values();
		for (AccountType accountType : allAccountType) {
		    if (accountType != acc.getAccountType()) {
		        Comboitem item = new Comboitem();
		        item.setLabel(accountType.name());
		        item.setValue(accountType.name()); // IMPORTANT
		        accountTypeComboBox.appendChild(item);
		    }
		}


		// set the branch name in combobox except current branch
		List<Branch> allBranch = branchDao.findAll();
		for (Branch myBranch : allBranch) {
		    if (!myBranch.getBranchId().equals(acc.getBranchId())) {
		        Comboitem item = new Comboitem();
		        item.setLabel(myBranch.getBranchName());
		        item.setValue(myBranch.getBranchName()); // or branchId if preferred
		        accountBranchBox.appendChild(item);
		    }
		}

	}

	/**
	 * Taking updatable data
	 * @throws SQLException 
	 */
	@Listen("onClick=#update")
	public void sendUpdateAccountReq() throws SQLException {
		if (!isFormValid())
			return;

		String accType = accountTypeComboBox.getSelectedItem().getValue();
		String accBranch = accountBranchBox.getSelectedItem().getValue();
		String accMode = accountModeBox.getSelectedItem().getValue();
		
		AccountUpdateRequest req = new AccountUpdateRequest();
		req.setAccountNo(accountNum);
		req.setNewAccountType(accType);
		req.setNewBranchName(accBranch);
		req.setNewModeOfOperation(accMode);
		System.out.println("Updating: " + accountNum);
		Long user_id = (Long) Executions.getCurrent().getSession().getAttribute("user_id");
		req.setRequestedBy(new UserServiceImpl().getUserByUserId(user_id));

		if (new RequestUpdateService().updateRequest(req)) {
			NotificationUtil.showInstant("info",
					"Requested submitted successfully.\nyour requested will be handled instantly.");

			Component root = getSelf();
			Include inc = (Include) root.getPage().getFellow("main_content_sec");
			inc.setSrc("/WEB-INF/components/view_all_account.zul");
		} else {
			NotificationUtil.showInstant("warning", "Requested is already submitted. Please check status");
			
			Component root = getSelf();
			Include inc = (Include) root.getPage().getFellow("main_content_sec");
			inc.setSrc("/WEB-INF/components/view_all_account.zul");
		}
	}

	@Listen("onClick=#cancel")
	public void cancelUpdateAccountReq() {
		Component root = getSelf();
		Include inc = (Include) root.getPage().getFellow("main_content_sec");
		inc.setSrc("/WEB-INF/components/view_all_account.zul");
	}

	public boolean isFormValid() {
		if (accountTypeComboBox.getSelectedItem() == null) {
			showWarning("Please select Account Type.");
			return false;
		}

		if (accountBranchBox.getSelectedItem() == null) {
			showWarning("Please select Branch.");
			return false;
		}

		if (accountModeBox.getSelectedItem() == null) {
			showWarning("Please select Mode of Operation.");
			return false;
		}
		return true;
	}

	// Helper to show warning messages
	private void showWarning(String msg) {
		NotificationUtil.showInstant("warning", msg);
	}
}