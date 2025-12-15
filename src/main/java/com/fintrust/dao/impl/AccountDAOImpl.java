package com.fintrust.dao.impl;


import java.sql.*;
import java.util.*;

import org.zkoss.zk.ui.util.Clients;

import com.fintrust.dao.AccountDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Account;
import com.fintrust.model.Account.AccountStatus;
import com.fintrust.model.Account.AccountType;


/**
 * JDBC implementation of AccountDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking standards.
 */
public class AccountDAOImpl implements AccountDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public AccountDAOImpl(Connection connection) {
        this.connection = connection;
    }
    
    public AccountDAOImpl() {
    		this(DBConnection.getConnection());
    }

    @Override
    public long create(Account account) throws SQLException {  
   
        String sql = """
            INSERT INTO accounts
            (user_id, branch_id, account_number, account_type, balance, nominee_id)
            VALUES (?, ?, ?, ?, ?,?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, account.getUserId());
            ps.setLong(2, account.getBranchId());
            ps.setLong(3, account.getAccountNumber());
            ps.setString(4, account.getAccountType().toString());
            ps.setDouble(5, account.getBalance());
            ps.setLong(6, account.getNominee_id());

            ps.executeUpdate();
          
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);  //return accountId
            }
        }
        return -1;
    }

    @Override
    public Account findByNumber(long accountNo) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowtoAccount(rs);
            }
        }
        return null;
    }
    
    @Override
    public Account findById(long accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowtoAccount(rs);
            }
        }
        return null;
    }
    public Account findByType(long userId, String type) throws SQLException{
    	   String sql = "SELECT * FROM accounts WHERE account_type = ? and user_id = ?";

           try (PreparedStatement ps = connection.prepareStatement(sql)) {
        	   ps.setString(1, type);
               ps.setLong(2, userId);
               try (ResultSet rs = ps.executeQuery()) {
                   if (rs.next()) return mapRowtoAccount(rs);
               }
           }
           return null;
       }        
    
    @Override
    public List<Account> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	accounts.add(mapRowtoAccount(rs));
                }
            }
        }
        return accounts;
    }
    
    @Override
    public List<Long> findByNumberUserId(long userId) throws SQLException {
        String sql = "SELECT account_number FROM accounts WHERE user_id = ?";
        
        List<Long> accounts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                	accounts.add(rs.getLong("account_number"));
                }
            }
        }
        return accounts;
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM accounts ORDER BY account_id ASC";
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
    public boolean update(long accountId, String accountType, String status, String currency) throws SQLException {
        String sql = """
            UPDATE accounts SET
                account_type = ?, status = ?, currency = ?
            WHERE account_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, accountType.toLowerCase());
            ps.setString(2, status.toLowerCase());
            ps.setString(3, currency);
            ps.setLong(4, accountId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateBalance(long accountId, double balance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, balance);
            ps.setLong(2, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * To generate new account no
     * @return
     */
    public Long getHighestAccountNo() {
		String query = "SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1";

		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			if (resultSet.next()) {
				long highestAccountNo = resultSet.getLong("account_number");
				// System.out.println("Highest Account No: " + highestAccountNo);
				return highestAccountNo;
			} else {
				return 0l;
			}
		} catch (SQLException e) {
			e.printStackTrace();			
		}
		return -1l; // return -1 if no record found or error occurred
	}
    
    /**
     * Mapping resultset to Account
     * @param rs
     * @return
     * @throws SQLException
     */
	private Account mapRowtoAccount(ResultSet rs) throws SQLException {

		Account account = new Account(rs.getLong("account_id"), rs.getLong("user_id"), rs.getLong("branch_id"),rs.getLong("nominee_id"),
				rs.getLong("account_number"), AccountType.valueOf(rs.getString("account_type").toUpperCase()), rs.getBigDecimal("balance").doubleValue(),
				rs.getString("currency"),AccountStatus.valueOf(rs.getString("status").toUpperCase()), rs.getTimestamp("opened_at").toLocalDateTime(),
				rs.getTimestamp("updated_at").toLocalDateTime());

		return account;
	}
	

    /**
     * Maps a ResultSet row into a Map representing the account record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("account_id", rs.getLong("account_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("branch_id", rs.getLong("branch_id"));
        map.put("account_number", rs.getLong("account_number"));
        map.put("account_type", rs.getString("account_type"));
        map.put("balance", rs.getBigDecimal("balance"));
        map.put("currency", rs.getString("currency"));
        map.put("status", rs.getString("status"));
        map.put("opened_at", rs.getTimestamp("opened_at"));
        map.put("updated_at", rs.getTimestamp("updated_at"));
        return map;
    }
	
}
