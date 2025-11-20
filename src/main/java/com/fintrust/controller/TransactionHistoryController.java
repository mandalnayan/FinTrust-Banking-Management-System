

package com.fintrust.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.*;

import com.fintrust.db.DBConnection;

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
       
    	transactionListbox.getItems().clear();

        String query = "SELECT id, from_account, to_account, amount, status, created_at FROM transactions where user";
        		

        if (from != null && to != null) {
            query += " WHERE DATE(created_at) BETWEEN ? AND ?";
        }


        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
        		
            if (from != null && to != null) {
                ps.setDate(1, from);
                ps.setDate(2, to);
            }

            ResultSet rs = ps.executeQuery();
          //  alert("Message fetched..!");
            List<Listitem> items = new ArrayList<>();

            while (rs.next()) {
                Listitem item = new Listitem();
                item.appendChild(new Listcell(String.valueOf(rs.getLong("id"))));
                item.appendChild(new Listcell(rs.getLong("from_account")+""));
                item.appendChild(new Listcell((rs.getLong("to_account") +"")));
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


