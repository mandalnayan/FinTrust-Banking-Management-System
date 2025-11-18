package com.fintrust.renderview;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.fintrust.model.Transaction;

public class TransactionRender implements ListitemRenderer<Transaction> {

	@Override
	public void render(Listitem row, Transaction transaction, int index) throws Exception {
		row.appendChild(new Listcell(String.valueOf(transaction.getId())));
		row.appendChild(new Listcell(String.valueOf(transaction.getFrom_account())));
		row.appendChild(new Listcell(String.valueOf(transaction.getTo_account())));
		row.appendChild(new Listcell(String.valueOf(transaction.getAmount())));
		row.appendChild(new Listcell(transaction.getStatus()));
		row.appendChild(new Listcell(transaction.getCreated_at()));
		
		if (transaction.getStatus().equalsIgnoreCase("SUCCESS")) {
			row.setStyle("background-color: green");
		}
		
	}

}
