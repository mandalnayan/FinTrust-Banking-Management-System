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
import com.fintrust.model.Account.AccountOwnershipType;
import com.fintrust.model.Account.AccountStatus;
import com.fintrust.model.Account.AccountType;
import com.fintrust.model.Branch;
import com.fintrust.model_copy.Account.ModeOfOperation;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.NomineeServiceImp;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;
import com.fintrust.model.Notification;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpenAccountComposer extends SelectorComposer<Component> {

	@Wire
	private Combobox accountType, branch, accountOwnershipType, nomineeRelation;

	@Wire
	private Longbox nomineeId;

	@Wire
	private Doublebox initialDeposit;

	@Wire
	private Textbox nomineeName,jointAccountHolderEmailIdTextbox;

	@Wire
	private Button btnSubmit, btnReset;
	
	@Wire 
	private Groupbox jointAccounHolderDetailsGroupBox;

	private final AccountServiceImpl accountService = new AccountServiceImpl();
	private final NomineeServiceImp nomineeService = new NomineeServiceImp();
	private final BranchDao branchDao = new BranchDao();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		AccountType accountTypesExistInBank[] = Account.AccountType.values();
		List<AccountType> allAccountTypeOfCurrentUser = accountService.getAllAccountType();
		for (AccountType at : accountTypesExistInBank) {
			if(!allAccountTypeOfCurrentUser.contains(at)) {
				accountType.appendChild(new Comboitem(at.name()));
			}
		}
		
		
		//set the all branch from db
		List<Branch> allBranch = branchDao.findAll();
		for (Branch accBranch : allBranch) {
			branch.appendChild(new Comboitem(accBranch.getBranchName()));
		}
		
		
		AccountOwnershipType accountOwnershipTypeInBank[] = Account.AccountOwnershipType.values();
		for (AccountOwnershipType at : accountOwnershipTypeInBank) {
			accountOwnershipType.appendChild(new Comboitem(at.name()));
		}
		
		accountType.setValue("Select");
		branch.setValue("Select");
		nomineeRelation.setValue("Select");
		accountOwnershipType.setValue("Select");
	}

	 @Listen("onChange=#accountOwnershipType") 
	 public void chageFileType() throws IOException{ 
		 if(accountOwnershipType.getSelectedItem().getLabel().equals("JOINT")) {
			 jointAccounHolderDetailsGroupBox.setVisible(true); 
		 } 
		 else {
			 jointAccounHolderDetailsGroupBox.setVisible(false);
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
			String ownerShip = accountOwnershipType.getSelectedItem().getLabel().toUpperCase();
			double deposit = initialDeposit.getValue();
			
			String nominee_name = nomineeName.getValue().trim();
			String relation = nomineeRelation.getValue().trim();
			long nomineeIdNum = nomineeId.longValue();
			Long nom_id = nomineeIdNum;
			

			//check that the user with same account type is already exists in db or not?
			Long userId  = (Long) Sessions.getCurrent().getAttribute("user_id");
			if(accountService.isAccountExists(userId, accType)){
				System.out.println("account exists aready ....................");
				String message = accType + " Account already exists with this user_id";
				NotificationUtil.push("info", message);
				resetForm();
				Executions.sendRedirect("");
			}
			
			//check that Joint account holder is already register or not on portal?
			if(accountOwnershipType.getSelectedItem().getLabel().equals("JOINT")){
				String jointAccountHolderUserId = jointAccountHolderEmailIdTextbox.getText();
				if(!new UserServiceImpl().isExistsUser(jointAccountHolderUserId)) {
					String message = "Please Register first joint Account Holder";
					NotificationUtil.push("info", message);
					resetForm();
					Executions.sendRedirect("");
				}
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
			long branchId = branchDao.findByBranchName(branchName).getBranchId();

			// Create Account object
			Account account = new Account();
			account.setAccountType(AccountType.valueOf(accType));
			account.setBalance(deposit);
			account.setNominee_id(nom_id);
			account.setBranchId(branchId);


			Notification notification = accountService.openAccount(account);
		
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
		if (accountOwnershipType.getSelectedItem() == null) {
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
		initialDeposit.setValue(null);
		nomineeName.setValue("");
		nomineeId.setText("");
		accountType.setValue("Select");
		branch.setValue("Select");
		nomineeRelation.setValue("Select");
		accountOwnershipType.setValue("Select");
	}
}
