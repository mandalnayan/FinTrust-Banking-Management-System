

package com.fintrust.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.*;

import com.fintrust.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryController extends SelectorComposer<Component> {

    private static final long serialVersionUID = 1123456776543234L;

	@Wire
    private Listbox transactionListbox;

    @Wire
    private Datebox fromDate;

    @Wire
    private Datebox toDate;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        loadTransactionData(null, null);
    }

  
    @Listen("onClick = #filterBtn")
    public void filterByDateRange() {
        java.util.Date from = fromDate.getValue();
        java.util.Date to = toDate.getValue();

        if (from == null || to == null) {
            Messagebox.show(" Please select both From and To dates.", "Missing Dates", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        if (to.before(from)) {
            Messagebox.show(" 'To Date' must be after 'From Date'.", "Invalid Range", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }

        loadTransactionData(new java.sql.Date(from.getTime()), new java.sql.Date(to.getTime()));
    }

   
    private void loadTransactionData(java.sql.Date from, java.sql.Date to) {
    	
    	Long user_id = (Long) Sessions.getCurrent().getAttribute("user_id");
       
    	transactionListbox.getItems().clear();

        String query = "SELECT transaction_id, account_number, counterparty_account_number, amount, status, created_at FROM transactions where user_id = ?";
        		

        if (from != null && to != null) {
            query += " AND DATE(created_at) BETWEEN ? AND ?";
        }
        query += " ORDER BY created_at DESC;";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
        		
        		ps.setLong(1, user_id);
            if (from != null && to != null) {
                ps.setDate(2, from);
                ps.setDate(3, to);
            }

            ResultSet rs = ps.executeQuery();
          //  alert("Message fetched..!");
            List<Listitem> items = new ArrayList<>();

            while (rs.next()) {
                Listitem item = new Listitem();
                item.appendChild(new Listcell(String.valueOf(rs.getLong("transaction_id"))));
                item.appendChild(new Listcell(rs.getLong("account_number")+""));
                item.appendChild(new Listcell((rs.getLong("counterparty_account_number") +"")));
                item.appendChild(new Listcell(String.format("%.2f", rs.getDouble("amount"))));
                item.appendChild(new Listcell(rs.getString("status")));
				 item.appendChild(new Listcell(rs.getTimestamp("created_at").toString())); 
                items.add(item);
            }

            transactionListbox.getItems().addAll(items);

            System.out.println("Transactions loaded: " + items.size());

        } catch (SQLException e) {
            e.printStackTrace();
            Messagebox.show( e.getMessage(),
                    "Database Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private String nullToDash(String value) {
        return (value == null || value.isEmpty()) ? "-" : value;
    }
}


