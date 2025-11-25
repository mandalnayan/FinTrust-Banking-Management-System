package com.fintrust.controller;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.model.Nominee;
import com.fintrust.model.Account.AccountStatus;
import com.fintrust.model.Account.AccountType;
import com.fintrust.model_copy.Account.ModeOfOperation;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.NomineeServiceImp;

import java.time.LocalDateTime;

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
	}

	// 🔹 Handle Submit button click
	@Listen("onClick = #btnSubmit")
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
			Long nom_id = null;

			Nominee nom = new Nominee(nomineeIdNum, nominee_name, relation);
			if (!nomineeService.isPresentNominee(nomineeIdNum)) {
				nom_id = nomineeService.saveNominee(nom);
			}			
			if (nom_id == -1) return;
			
		

			// Create Account object
			Account account = new Account();
			account.setAccountType(accType);
			account.setBalance(deposit);			
			account.setNominee_id(nom_id);
			boolean success = acconntService.openAccount(account);

			if (success) {
				Messagebox.show("Account created successfully!", "Success", Messagebox.OK, Messagebox.INFORMATION);
				resetForm();
				Executions.sendRedirect("");
			} else {
				Messagebox.show("Account creation failed! Please try again.", "Database Error", Messagebox.OK,
						Messagebox.ERROR);
			}

		} catch (IllegalArgumentException e) {
			Messagebox.show("Invalid input: " + e.getMessage(), "Validation Error", Messagebox.OK,
					Messagebox.EXCLAMATION);
		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("Error while creating account: " + e.getMessage(), "Exception", Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	// Handle Reset button click
	@Listen("onClick = #btnReset")
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
