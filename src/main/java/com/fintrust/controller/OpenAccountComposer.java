package com.fintrust.controller;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.fintrust.dao.impl.BranchDao;
import com.fintrust.model.Account;
import com.fintrust.model.Nominee;
import com.fintrust.model.Account.AccountStatus;
import com.fintrust.model.Account.AccountType;
import com.fintrust.model.Branch;
import com.fintrust.model_copy.Account.ModeOfOperation;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.NomineeServiceImp;
import com.fintrust.util.NotificationUtil;
import com.fintrust.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

public class OpenAccountComposer extends SelectorComposer<Component> {

	@Wire
	private Combobox accountType, branch, modeOfOperation, nomineeRelation;

	@Wire
	private Longbox nomineeId;

	@Wire
	private Doublebox initialDeposit;

	@Wire
	private Textbox nomineeName;

	@Wire
	private Button btnSubmit, btnReset;

	private final AccountServiceImpl acconntService = new AccountServiceImpl();
	private final NomineeServiceImp nomineeService = new NomineeServiceImp();
	private final BranchDao BranchDao = new BranchDao();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		modeOfOperation.setSelectedIndex(0); // Default value

		AccountType accountTypes[] = Account.AccountType.values();
		System.out.println("Types: " + accountTypes.length);
		for (AccountType at : accountTypes) {
			accountType.appendChild(new Comboitem(at.name()));
		}
		accountType.setSelectedIndex(0);
		
		List<Branch> allBranch = BranchDao.findAll();
		for(Branch myBranch : allBranch) {
			branch.appendChild(new Comboitem(myBranch.getBranchName()));
		}
	}

	// Handle Submit button click
	@Listen("onClick = #btnAccountSubmit")
	public void onSubmit() {
		if (!isFormValid())
			return;

		try {
			// Collecting form data
			String accType = accountType.getSelectedItem().getLabel().toUpperCase();
			String branchName = branch.getSelectedItem().getLabel();
			String mode = modeOfOperation.getSelectedItem().getLabel().toUpperCase();
			double deposit = initialDeposit.getValue();

			String nominee_name = nomineeName.getValue().trim();
			String relation = nomineeRelation.getValue().trim();
			long nomineeIdNum = nomineeId.longValue();
			Long nom_id = nomineeIdNum;
			
			Long userId  = (Long) Sessions.getCurrent().getAttribute("user_id");
			if(acconntService.isAccountExists(userId, accType)){
				System.out.println("account exists aready ....................");
				String message = accType + " Account already exists with this user_id";
				NotificationUtil.push("info", message);
				resetForm();
				Executions.sendRedirect("");
			}

			// check given nominee id already exist in db or not?
			Nominee nom = new Nominee(nomineeIdNum, nominee_name, relation);
			nom_id = nomineeService.isPresentNominee(nomineeIdNum);
			if (nom_id == null) {
				nom_id = nomineeService.saveNominee(nom);
			}
			if (nom_id == -1l)
				return;

			// GET THE Brach_id using branch name
			long branchId = BranchDao.findByBranchName(branchName).getBranchId();

			// Create Account object
			Account account = new Account();
			account.setAccountType(AccountType.valueOf(accType));
			account.setBalance(deposit);
			account.setNominee_id(nom_id);
			account.setBranchId(branchId);


			Notification notification = acconntService.openAccount(account);
		
			if (!notification.getType().equals("warning")) {
				NotificationUtil.push(notification);
				Executions.sendRedirect("");			
	
			if (notification.getType().equals("info")) {
				String message = "Account created successfully!";
				NotificationUtil.push("info", message);				
				resetForm();
				Executions.sendRedirect("");				

			} else {
				NotificationUtil.showInstant(notification);
			}
			}
		} catch (IllegalArgumentException e) {
			String message = "Please give valid input!";
			NotificationUtil.push("error", message);
			e.printStackTrace();

		} catch (Exception e) {
			String message = "Server error. Failed to create Account. Please try again!";
			NotificationUtil.push("error", message);

			e.printStackTrace();
		}
	}

	// Handle Reset button click
	@Listen("onClick = #btnAccountReset")
	public void onReset() {
		resetForm();
	}

	// Validate the form fields
	private boolean isFormValid() {
		if (accountType.getSelectedItem() == null) {
			showWarning("Please select Account Type.");
			return false;
		}
		if (branch.getSelectedItem() == null) {
			showWarning("Please select Branch.");
			return false;
		}
		if (initialDeposit.getValue() == null || initialDeposit.getValue() < 1000) {
			showWarning("Minimum initial deposit must be ₹1000 or above.");
			return false;
		}
		if (modeOfOperation.getSelectedItem() == null) {
			showWarning("Please select Mode of Operation.");
			return false;
		}
		if (nomineeName.getValue().trim().isEmpty()) {
			showWarning("Nominee Name cannot be empty.");
			return false;
		}
		if (!nomineeName.getValue().trim().matches("[a-z-A-Z ]+")) {
			showWarning("Nominee Name cannot have other than character");
			return false;
		}
		if (nomineeRelation.getSelectedItem() == null) {
			showWarning("Please select the Nominee Relation");
			return false;
		}
		if (nomineeId.getValue() == null) {
			showWarning("Nominee Id cannot be empty");
			return false;
		}
		if (String.valueOf(nomineeId.getValue()).length() != 12) {
			showWarning("Nominee Id must be 12 digit");
			return false;
		}
		return true;
	}

	// Helper to show warning messages
	private void showWarning(String msg) {
		Messagebox.show(msg, "Validation Error", Messagebox.OK, Messagebox.EXCLAMATION);
	}

	// Reset the form
	private void resetForm() {
		accountType.setSelectedIndex(-1);
		branch.setSelectedIndex(-1);
		initialDeposit.setValue(null);
		modeOfOperation.setSelectedIndex(-1);
		nomineeName.setValue("");
		nomineeId.setText("");
		nomineeRelation.setValue("");
	}
}
