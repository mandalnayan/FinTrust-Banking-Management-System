package com.fintrust.controller;

import com.fintrust.dao.impl.TransactionDAO;
import com.fintrust.model.Transaction_copy;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

public class TransactionHistoryController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1123456776543234L;

    @Wire
    private Listbox transactionListbox;

    @Wire
    private Datebox fromDate;

    @Wire
    private Datebox toDate;

    private TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        loadTransactions(null, null);
    }

    @Listen("onClick = #filterBtn")
    public void filterByDateRange() {

        java.util.Date from = fromDate.getValue();
        java.util.Date to = toDate.getValue();

        if (from == null || to == null) {
            Messagebox.show("Please select both From and To dates.");
            return;
        }

        if (to.before(from)) {
            Messagebox.show("'To Date' must be after 'From Date'.");
            return;
        }

        loadTransactions(
                new java.sql.Date(from.getTime()),
                new java.sql.Date(to.getTime())
        );
    }

    private void loadTransactions(java.sql.Date from, java.sql.Date to) {

        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");

        transactionListbox.getItems().clear();

        List<Transaction_copy> transaction_copies =
                transactionDAO.getTransactions(userId, from, to);

        for (Transaction_copy t : transaction_copies) {
            Listitem item = new Listitem();
            item.appendChild(new Listcell(String.valueOf(t.getTransactionId())));
            item.appendChild(new Listcell(String.valueOf(t.getAccountNumber())));
            item.appendChild(new Listcell(String.valueOf(t.getCounterparty_account_number())));
            String amountIcon = "+", amountColor = "green";
            
            if (t.getTxnType().equalsIgnoreCase("debit")) {
            	amountIcon = "-";
            	amountColor = "red";
            }
            
            String amount = amountIcon + String.format("%.2f", t.getAmount()) + "₹"; 
            Listcell amountLC = new Listcell(amount);
            amountLC.setStyle("color:" + amountColor);
            item.appendChild(amountLC);
            item.appendChild(new Listcell(t.getStatus()));
            item.appendChild(new Listcell(t.getCreatedAt().toString()));

            transactionListbox.appendChild(item);
        }
    }
}
