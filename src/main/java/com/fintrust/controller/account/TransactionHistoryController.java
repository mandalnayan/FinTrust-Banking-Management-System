
package com.fintrust.controller.account;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.*;

import com.fintrust.dao.TransactionHistoryDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryController extends SelectorComposer<Component> {

	@Wire
	private Listbox transactionListbox;

	@Wire
	private Datebox fromDate;

	@Wire
	private Datebox toDate;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<Transaction> transactions = TransactionHistoryDAO.loadTransactionData(null, null);
		transactionListbox.getItems().clear();
		
		transactionListbox.setModel(new ListModelList(transactions));
	}

	@Listen("onClick = #filterBtn")
	public void filterByDateRange() {
		java.util.Date from = fromDate.getValue();
		java.util.Date to = toDate.getValue();

		if (from == null || to == null) {
			Messagebox.show(" Please select both From and To dates.", "Missing Dates", Messagebox.OK,
					Messagebox.EXCLAMATION);
			return;
		}

		if (to.before(from)) {
			Messagebox.show(" 'To Date' must be after 'From Date'.", "Invalid Range", Messagebox.OK,
					Messagebox.EXCLAMATION);
			return;
		}

		List<Transaction> transactions = TransactionHistoryDAO.loadTransactionData(new java.sql.Date(from.getTime()), new java.sql.Date(to.getTime()));
		transactionListbox.setModel(new ListModelList(transactions));
	}

	

	private String nullToDash(String value) {
		return (value == null || value.isEmpty()) ? "-" : value;
	}
}
