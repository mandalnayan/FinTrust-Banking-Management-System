package com.fintrust.dao.impl;

import java.sql.*;

import com.fintrust.db.DBConnection;
import com.fintrust.util.NotificationUtil;

public class FundTransferDAO {

    /**
     * Transfers amount from fromAcc to toAcc in a single transactional operation.
     * Returns true if transfer committed successfully, false otherwise.
     */
    public static boolean transferFunds(Long fromAcc, Long toAcc, double amount) {
       
       
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                System.out.println("Database connection failed.");
                return false;
            }

            conn.setAutoCommit(false);

            
            double senderBalance = 0;
            String sqlCheckSender = "SELECT balance FROM accounts WHERE account_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCheckSender)) {
                ps.setLong(1, fromAcc);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        senderBalance = rs.getDouble("balance");
                    } else {
                        System.out.println("Sender account not found: " + fromAcc);
                        conn.rollback();
                        return false;
                    }
                }
            }

            if (senderBalance < amount) {
            	NotificationUtil.showInstant("warning", " Insufficient balance in account: ");
                conn.rollback();
                return false;
            }

            
            String sqlCheckReceiver = "SELECT 1 FROM accounts WHERE account_number = ?";
            boolean receiverExists = false;
            try (PreparedStatement ps = conn.prepareStatement(sqlCheckReceiver)) {
            	 ps.setLong(1, toAcc);
                try (ResultSet rs = ps.executeQuery()) {
                    receiverExists = rs.next();
                }
            }
            if (!receiverExists) {
                System.out.println("Receiver account not found: " + toAcc);
                conn.rollback();
                return false;
            }

            
            String sqlDebit = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            int debitRows;
            try (PreparedStatement ps = conn.prepareStatement(sqlDebit)) {
                ps.setDouble(1, amount);
                ps.setLong(2,fromAcc);
                debitRows = ps.executeUpdate();
            }
            System.out.println("Debit rows affected = " + debitRows);

            if (debitRows <= 0) {
                System.out.println(" Debit failed - no sender row updated.");
                conn.rollback();
                return false;
            }

            
            String sqlCredit = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            int creditRows;
            try (PreparedStatement ps = conn.prepareStatement(sqlCredit)) {
                ps.setDouble(1, amount);
                ps.setLong(2, toAcc);
                creditRows = ps.executeUpdate();
            }
            System.out.println(" Credit rows affected = " + creditRows);

           
            String status = (creditRows > 0) ? "SUCCESS" : "FAILED";
            String sqlTxn = "INSERT INTO transactions(counterparty_account_id, account_id, amount, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTxn)) {
                ps.setLong(1, fromAcc);
                ps.setLong(2, toAcc);
                ps.setDouble(3, amount);
                ps.setString(4, status);
                ps.executeUpdate();
            }

            // 6️⃣ Commit or rollback
            if (creditRows > 0) {
                conn.commit();
                System.out.println("Transfer successful: " + fromAcc + " → " + toAcc + " | Amount: " + amount);
                return true;
            } else {
                conn.rollback();
                System.out.println("Credit failed transaction rolled back.");
                return false;
            }

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}



