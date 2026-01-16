package com.fintrust.controller;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.model.Beneficiary;
import com.fintrust.model.Notification;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.BeneficiaryService;
import com.fintrust.service.FundTransferService;
import com.fintrust.util.NotificationUtil;

public class FundTransferController extends SelectorComposer<Component> {

	private static final long serialVersionUID = 7007232702111772233L;

	@Wire
	private Combobox accountsComboBox, beneficiaryComboBox;

	@Wire
	private Longbox toAccountBox;
	@Wire
	private Textbox ifsccodeBox;
	@Wire
	private Doublebox amountBox;
	@Wire
	private Label statusLabel;

	private Long fromAccount;

	private AccountService accountService;
	private BeneficiaryService beneficiaryService;

	private List<Beneficiary> beneficiaries;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		accountService = new AccountServiceImpl();
		List<Account> accounts = accountService.getAllUserAccounts();
		System.out.println(accounts);
		beneficiaryService = new BeneficiaryService();

		if (accounts == null || accounts.size() == 0) {
			NotificationUtil.showInstant("error", "Internal server error!");
		} else {
			for (Account account : accounts) {
				Comboitem item = accountsComboBox.appendItem(account.getAccountNumber() + "");
				item.setValue(account.getAccountNumber()); // <-- THIS FIXES NULL PROBLEM
			}

			beneficiaries = beneficiaryService.getBeneficiaries();

			for (Beneficiary b : beneficiaries) {
				Comboitem item = new Comboitem(b.getUserId() + ". " + b.getName() + " (" + b.getBankName() + ")");
				item.setValue(b);
				beneficiaryComboBox.appendChild(item);
			}
		}
	}

	@Listen("onSelect=#accountsComboBox")
	public void onAccountSelect() {
		Comboitem selected = accountsComboBox.getSelectedItem();
		if (selected != null) {
			fromAccount = selected.getValue();
			System.out.println("acc " + fromAccount);
		}
	}

	@Listen("onSelect=#beneficiaryComboBox")
	public void onBeneficiarySelect() {
		Comboitem selected = beneficiaryComboBox.getSelectedItem();
		if (selected != null) {
			Beneficiary b = selected.getValue();
			toAccountBox.setValue(b.getAccountNumber());
			ifsccodeBox.setValue(b.getIfscCode());
			// Clients.showNotification("Beneficiary selected: " + b.getName());
		}
	}

	@Listen("onClick=#transferBtn")
	public void transferFunds() {
		Long fromAcc = fromAccount;
		Long toAcc = toAccountBox.getValue();
		Double amt = amountBox.getValue();
		String ifscCode = ifsccodeBox.getValue();
		
		Notification notification = isValid(fromAcc, toAcc, amt, ifscCode);
		

		if (!notification.getType().equals("info")) {
			NotificationUtil.showInstant(notification);
			return;
		} else {
			NotificationUtil.showInstant("info", "Processing..");  
		}

		boolean result = new FundTransferService().transferFunds(fromAcc, toAcc, ifscCode, amt);

		if (result) {
			NotificationUtil.push("info", "Transfer successfull!");
			Executions.sendRedirect("/user/userDashboard.zul");
		}
	}
	
	@Listen("onClick=#clearBtn")
	public void clear() {
		accountsComboBox.setValue(null);
		beneficiaryComboBox.setValue(null);
		toAccountBox.setValue(null);
		amountBox.setValue(null);	
	}
	
	@Listen("onClick=#addBeneiciary")
	public void addBeneiciary() {
		Include mainContentSec = (Include) Sessions.getCurrent().getAttribute("main_content_sec");

		mainContentSec.setSrc("/WEB-INF/components/addBeneficiary.zul");
	}

	public Notification isValid(Long fromAcc, Long toAcc, Double amt, String ifscCode) {
		String res = "";
		if (fromAcc == null || String.valueOf(fromAcc).length() != 12) res = "Sender account number is invalid";
		else if(toAcc == null || String.valueOf(toAcc).length() != 12) res = "Receiver account number is invalid";
		else if(amt == null || amt < 1) res = "Invalid amount";
		else if(ifscCode == null || ifscCode.length() != 11) res = "Please enter valid IFSC code";
		String notificationType = res.isBlank() ? "info" : "error";
		return new Notification(notificationType, res);
	}
}
