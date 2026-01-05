package com.fintrust.dao.impl;

import java.sql.*;

import com.fintrust.db.DBConnection;
import com.fintrust.util.NotificationUtil;

public class FundTransferDAO {

	private Connection connection;
    public FundTransferDAO(Connection conn){
        connection = conn;
    }

    public double getAccountBalance(Long accountNumber)
            throws SQLException {

        String sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Account not found: " + accountNumber);
                }
                return rs.getDouble("balance");
            }
        }
    }

    public long getBranchId(Long accountNumber)
            throws SQLException {

        String sql = "SELECT branch_id FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Receiver account not found");
                }
                return rs.getLong("branch_id");
            }
        }
    }

    public boolean validateIFSC(long branchId, String ifsc)
            throws SQLException {

        String sql = "SELECT 1 FROM branches WHERE branch_id = ? AND ifsc_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, branchId);
            ps.setString(2, ifsc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int debit(Long accountNumber, double amount)
            throws SQLException {

        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? and status = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setLong(2, accountNumber);
            return ps.executeUpdate();
        }
    }

    public int credit(Long accountNumber, double amount)
            throws SQLException {

        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ? and status = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setLong(2, accountNumber);
            int resValue = ps.executeUpdate();
            return resValue;
        }
    }

    public boolean isAccountActive(Long accountNumber) throws SQLException {
    	String sql = "SELECT 1 FROM accounts WHERE account_number = ? and status = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    public void insertTransaction(Long userId,
                                  Long fromAcc,
                                  Long toAcc,
                                  double amount,
                                  String txn_type, double remaningBalance,
                                  String status) throws SQLException {
    	

        String sql = """
            INSERT INTO transactions
            (user_id, account_number, counterparty_account_number, amount,balance_after, txn_type, status)
            VALUES (?, ?, ?, ?,?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, fromAcc);
            ps.setLong(3, toAcc);
            ps.setDouble(4, amount);
            ps.setDouble(5, remaningBalance);
            ps.setString(6, txn_type);
            ps.setString(7, status);
            ps.executeUpdate();
        }
    }
}
