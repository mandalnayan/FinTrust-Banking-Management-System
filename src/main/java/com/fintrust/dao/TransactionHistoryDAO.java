package com.fintrust.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction;

public class TransactionHistoryDAO {

	public static List<Transaction> loadTransactionData(java.sql.Date from, java.sql.Date to) {

		String query = "SELECT id, from_account, to_account, amount, status, created_at FROM transactions where";
		List<Transaction> transactions = new ArrayList<>();
		
		if (from != null && to != null) {
			query += " DATE(created_at) BETWEEN ? AND ? AND";
		}
		
		query += " customer_id = ?";
		Long customer_id = (Long)Sessions.getCurrent().getAttribute("customer_id");
		
//		If we got customer_id is null
		if (customer_id == null) return null;
		
		//query += " Order By created_at DESC";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {

			if (from != null && to != null) {
				ps.setDate(1, from);
				ps.setDate(2, to);
				ps.setLong(3, customer_id);
			} else {
				ps.setLong(1, customer_id);
			}

			ResultSet rs = ps.executeQuery();			

			while (rs.next()) {
				Transaction transaction = new Transaction(rs.getLong("id"), rs.getLong("from_account"),
						rs.getLong("from_account"), rs.getDouble("amount"), rs.getString("status"),
						rs.getTimestamp("created_at").toString());
				transactions.add(transaction);
			}

			return transactions;

		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage(), "Database Error", Messagebox.OK, Messagebox.ERROR);
		}
		return null;
	}
}
