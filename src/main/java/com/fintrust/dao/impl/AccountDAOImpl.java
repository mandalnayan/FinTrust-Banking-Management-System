package com.fintrust.dao.impl;


import java.sql.*;
import java.util.*;

import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

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
	private static final String USER_ID = "user_id";
	private static final String ACCOUNT_NO = "account_number";
	private static final String STATUS = "status";
	private static final String BRANCH_ID = "branch_id";
	private static final String ACCOUNT_TYPE = "account_type";
	private static final String BALANCE = "balance";
	private static final String NOMINEE_ID = "nominee_id";
	private static final String CURRENCY = "currency";
	private static final String ACCOUNT_ID = "account_id";
	private static final String UPDATED_AT = "updated_at";
	private static final String OPENED_AT = "opened_at";

    @Override	
	public List<String> issuedCardTypeByAct(long actNumber)
	{
		List<String> cardTypeList=new ArrayList();
		String q="select card_type from cards where account_number=? and card_status <> ? ";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(q)) {

			statement.setLong(1, actNumber);
		    statement.setString(2, "expired");
			ResultSet rs = statement.executeQuery();
			while(rs.next()) {
				cardTypeList.add(rs.getString("card_type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		System.out.println(cardTypeList);
		return cardTypeList;
		
	}


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
    
    /**
     * Finds the user ID associated with the given account number.
     *
     * @param accountNo the account number
     * @return the user ID if found, otherwise {@code null}
     * @throws SQLException if a database access error occurs
     */
    public Long findUserIdByAccountNo(Long accountNo) throws SQLException {
        String sql = "SELECT user_id FROM accounts WHERE account_number = ? LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long userId = rs.getLong(USER_ID);
                    return rs.wasNull() ? null : userId;
                }
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
    
    /**
     * Get account by userId and account type
     */
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
                	accounts.add(rs.getLong(ACCOUNT_NO));
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
				long highestAccountNo = resultSet.getLong(ACCOUNT_NO);
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

		Account account = new Account(rs.getLong(ACCOUNT_ID), rs.getLong(USER_ID), rs.getLong(BRANCH_ID),rs.getLong(NOMINEE_ID),
				rs.getLong(ACCOUNT_NO), AccountType.valueOf(rs.getString(ACCOUNT_TYPE).toUpperCase()), rs.getBigDecimal(BALANCE).doubleValue(),
				rs.getString(CURRENCY),AccountStatus.valueOf(rs.getString(STATUS).toUpperCase()), rs.getTimestamp(OPENED_AT).toLocalDateTime(),
				rs.getTimestamp(UPDATED_AT).toLocalDateTime());

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
        map.put(ACCOUNT_ID, rs.getLong(ACCOUNT_ID));
        map.put(USER_ID, rs.getLong(USER_ID));
        map.put(BRANCH_ID, rs.getLong(BRANCH_ID));
        map.put(ACCOUNT_NO, rs.getLong(ACCOUNT_NO));
        map.put(ACCOUNT_TYPE, rs.getString(ACCOUNT_TYPE));
        map.put(BALANCE, rs.getBigDecimal(BALANCE));
        map.put(CURRENCY, rs.getString(CURRENCY));
        map.put(STATUS, rs.getString(STATUS));
        map.put(OPENED_AT, rs.getTimestamp(OPENED_AT));
        map.put(UPDATED_AT, rs.getTimestamp(UPDATED_AT));
        return map;
    }
	
}
