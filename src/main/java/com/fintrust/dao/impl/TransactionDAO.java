package com.fintrust.dao.impl;

import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction_copy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public List<Transaction_copy> getTransactions(Long userId, Date from, Date to) {

        List<Transaction_copy> transaction_copies = new ArrayList<>(); 

        String sql = "SELECT * FROM transactions WHERE user_id = ?";

        if (from != null && to != null) {
            sql += " AND DATE(created_at) BETWEEN ? AND ?";
        }

        sql += " ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            if (from != null && to != null) {
                ps.setDate(2, from);
                ps.setDate(3, to);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Transaction_copy t = new Transaction_copy();
                t.setTransactionId(rs.getLong("transaction_id"));
                t.setAccountNumber(rs.getLong("account_number"));
                t.setCounterparty_account_number(rs.getLong("counterparty_account_number"));
                t.setTxnType(rs.getString("txn_type"));   
                t.setMode(rs.getString("mode"));
                t.setAmount(rs.getDouble("amount"));
                t.setStatus(rs.getString("status"));
                t.setCreatedAt(rs.getTimestamp("created_at"));

                transaction_copies.add(t);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching transactions", e);
        }

        return transaction_copies;
    }
}

