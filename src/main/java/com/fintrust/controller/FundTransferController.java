package com.fintrust.controller;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.fintrust.model.Account;
import com.fintrust.model.Beneficiary;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.BeneficiaryService;
import com.fintrust.service.FundTransferService;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.AccountDAO;
import com.fintrust.dao.impl.AccountDAOImpl;
import com.fintrust.dao.impl.FundTransferDAO;

public class FundTransferController extends SelectorComposer<Component> {

	@Wire
	private Combobox accountList, beneficiaryComboBox;

	@Wire
	private Longbox toAccountBox;
	@Wire
	private Textbox ifsccodeBox;
	@Wire
	private Doublebox amountBox;
	@Wire
	private Label statusLabel;

	private Long fromAccount;

	private AccountDAO accountDao;
	private AccountService accountService;
	private BeneficiaryService beneficiaryService;

	private List<Beneficiary> beneficiaries;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		accountDao = new AccountDAOImpl();
		accountService = new AccountServiceImpl();
		List<Account> accounts = accountService.getAllAccounts();
		beneficiaryService = new BeneficiaryService();

		if (accounts == null || accounts.size() == 0) {
			NotificationUtil.showInstant("error", "Internal server error!");
		} else {
			for (Account account : accounts) {
				Comboitem item = accountList.appendItem(account.getAccountNumber() + "");
				item.setValue(account.getAccountNumber()); // <-- THIS FIXES NULL PROBLEM
			}

			beneficiaries = beneficiaryService.getBeneficiaries();

			for (Beneficiary b : beneficiaries) {
				Comboitem item = new Comboitem(b.getName() + " (" + b.getBankName() + ")");
				item.setValue(b);
				beneficiaryComboBox.appendChild(item);
			}
		}
	}

	@Listen("onSelect=#accountList")
	public void onAccountSelect() {
		// System.out.println("invoked fundtranser");
		Comboitem selected = accountList.getSelectedItem();
		if (selected != null) {
			fromAccount = selected.getValue();
			System.out.println("acc " + fromAccount);
		}
	}

	@Listen("onSelect=#beneficiaryCombo")
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

		if (fromAcc == null || toAcc == null || String.valueOf(toAcc).length() != 12 || amt == null || amt <= 0
				|| fromAcc.equals(toAcc)) {

			NotificationUtil.showInstant("error", "Please enter valid transfer details!");
			return;
		}

		boolean result = new FundTransferService().transferFunds(fromAcc, toAcc, ifscCode, amt);

		if (result) {
			NotificationUtil.push("info", "Transfer successfull!");
			Executions.sendRedirect("");

		} else {
			NotificationUtil.showInstant("error", "Transfer failed! Check balance or account.");
		}
	}
}
