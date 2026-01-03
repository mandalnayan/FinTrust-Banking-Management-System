package com.fintrust.dao.impl;



import java.sql.*;
import java.util.*;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.TransactionsDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction;


/**
 * JDBC implementation of TransactionsDAO for banking systems.
 * <p>
 * Implements all CRUD operations securely using PreparedStatements
 * and follows banking-grade standards.
 */
public class TransactionsDAOImpl implements TransactionsDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    
    public TransactionsDAOImpl(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public long create(long accountId, Long relatedAccountId, Long beneficiaryId,
                       String txnReference, String txnType, String mode,
                       double amount, double balanceAfter, String description,
                       String status) throws SQLException {

        String sql = """
            INSERT INTO transactions
            (account_id, related_account_id, beneficiary_id, txn_reference,
             txn_type, mode, amount, balance_after, description, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, accountId);
            if (relatedAccountId != null) ps.setLong(2, relatedAccountId); else ps.setNull(2, Types.BIGINT);
            if (beneficiaryId != null) ps.setLong(3, beneficiaryId); else ps.setNull(3, Types.BIGINT);
            ps.setString(4, txnReference);
            ps.setString(5, txnType.toLowerCase());
            ps.setString(6, mode.toLowerCase());
            ps.setDouble(7, amount);
            ps.setDouble(8, balanceAfter);
            ps.setString(9, description);
            ps.setString(10, status.toLowerCase());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    public List<Transaction> allCurrentUserTransactions(java.sql.Date from, java.sql.Date to) throws SQLException {

        List<Transaction> list = new ArrayList<>();

//        Session session = Executions.getCurrent().getSession();
//        Long userId = Long.parseLong((String) session.getAttribute("user_id"));

//        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY created_at DESC";
        
        String sql = "SELECT * FROM transactions WHERE user_id = ?";

      if (from != null && to != null) {
    	  sql += " AND DATE(created_at) BETWEEN ? AND ?";
      }

      sql += " ORDER BY created_at DESC";

        

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
        	  pst.setLong(1, 2);         //take it from session..............
        	 if (from != null && to != null) {
        		 pst.setDate(2, from);
        		 pst.setDate(3, to);
             }
        	
        	
         
            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {
                    Transaction tx = new Transaction();

                    tx.setTransactionId(rs.getLong("transaction_id"));
                    tx.setUserId(rs.getLong("user_id"));
                    tx.setAccountNumber(rs.getLong("account_number"));

                    // Nullable columns
                    tx.setCounterpartyAccountNumber(
                            rs.getObject("counterparty_account_number", Long.class)
                    );
                    tx.setBeneficiaryId(
                            rs.getObject("beneficiary_id", Long.class)
                    );

                    tx.setTxnReference(rs.getString("txn_reference"));
                    tx.setTxnType(rs.getString("txn_type"));
                    tx.setMode(rs.getString("mode"));
                    tx.setAmount(rs.getBigDecimal("amount"));
                    tx.setBalanceAfter(rs.getBigDecimal("balance_after"));
                    tx.setDescription(rs.getString("description"));
                    tx.setStatus(rs.getString("status"));

                    // TIMESTAMP → LocalDateTime
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        tx.setCreatedAt(ts.toLocalDateTime());
                    }

                    list.add(tx);
                }
            }
        }

        return list;
    }

    
    
    @Override
    public Map<String, Object> findById(long transactionId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }

        return null;
    }
   
    @Override
    public List<Map<String, Object>> findByAccountId(long accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    @Override
    public boolean updateStatus(long transactionId, String status) throws SQLException {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toLowerCase());
            ps.setLong(2, transactionId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, transactionId);
            return ps.executeUpdate() > 0;
        }
    }
//    public static void main(String[] args) {
//    	TransactionsDAOImpl ob=new TransactionsDAOImpl(DBConnection.getConnection());
//    	try {
//			List<Transaction> list=ob.allCurrentUserTransactions();
//			System.out.println(list);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//	}
    /**
     * Maps a ResultSet row into a Map representing the transaction record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("transaction_id", rs.getLong("transaction_id"));
        map.put("account_id", rs.getLong("account_id"));
        map.put("related_account_id", rs.getObject("related_account_id"));
        map.put("beneficiary_id", rs.getObject("beneficiary_id"));
        map.put("txn_reference", rs.getString("txn_reference"));
        map.put("txn_type", rs.getString("txn_type"));
        map.put("mode", rs.getString("mode"));
        map.put("amount", rs.getBigDecimal("amount"));
        map.put("balance_after", rs.getBigDecimal("balance_after"));
        map.put("description", rs.getString("description"));
        map.put("status", rs.getString("status"));
        map.put("created_at", rs.getTimestamp("created_at"));
        return map;
    }

//    public List<Transaction> allCurrentUserTransactions() throws SQLException {
//
//        List<Transaction> list = new ArrayList<>();
//
////        Session session = Executions.getCurrent().getSession();
////        Long userId = Long.parseLong((String) session.getAttribute("user_id"));
//
//        String sql = "SELECT * FROM transactions WHERE user_id = ? ORDER BY created_at DESC";
//
//        try (PreparedStatement pst = connection.prepareStatement(sql)) {
//
//            pst.setLong(1, 2);
//
//            try (ResultSet rs = pst.executeQuery()) {
//
//                while (rs.next()) {
//                    Transaction tx = new Transaction();
//
//                    tx.setTransactionId(rs.getLong("transaction_id"));
//                    tx.setUserId(rs.getLong("user_id"));
//                    tx.setAccountNumber(rs.getLong("account_number"));
//
//                    // Nullable columns
//                    tx.setCounterpartyAccountNumber(
//                            rs.getObject("counterparty_account_number", Long.class)
//                    );
//                    tx.setBeneficiaryId(
//                            rs.getObject("beneficiary_id", Long.class)
//                    );
//
//                    tx.setTxnReference(rs.getString("txn_reference"));
//                    tx.setTxnType(rs.getString("txn_type"));
//                    tx.setMode(rs.getString("mode"));
//                    tx.setAmount(rs.getBigDecimal("amount"));
//                    tx.setBalanceAfter(rs.getBigDecimal("balance_after"));
//                    tx.setDescription(rs.getString("description"));
//                    tx.setStatus(rs.getString("status"));
//
//                    // TIMESTAMP → LocalDateTime
//                    Timestamp ts = rs.getTimestamp("created_at");
//                    if (ts != null) {
//                        tx.setCreatedAt(ts.toLocalDateTime());
//                    }
//
//                    list.add(tx);
//                }
//            }
//        }
//
//        return list;
//    }

    public static void main(String[] args) {
    	
    	TransactionsDAOImpl ob=new TransactionsDAOImpl(DBConnection.getConnection());
    	 try {
			List<Transaction> txx= ob.allCurrentUserTransactions(null,null);
			System.out.println(txx);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
	}
	
}
