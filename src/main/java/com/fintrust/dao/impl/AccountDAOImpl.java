package com.fintrust.dao.impl;

import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fintrust.dao.AccountDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Account;
import com.fintrust.model.Account.AccountStatus;
import com.fintrust.model.Account.AccountType;

/**
 * JDBC implementation of AccountDAO.
 *
 * This class provides secure CRUD operations for Account entities using
 * PreparedStatements and follows standard banking security practices.
 */
public class AccountDAOImpl implements AccountDAO {

    private static final Logger logger = LoggerFactory.getLogger(AccountDAOImpl.class);

    /* Column name constants */
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

    private static final String CARD_STATUS_EXPIRED = "expired";

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection
     */
    public AccountDAOImpl(Connection connection) {
        this.connection = connection;
    }

    /**
     * Default constructor using DBConnection.
     */
    public AccountDAOImpl() {
        this(DBConnection.getConnection());
    }

    /**
     * Returns issued card types for a given account number.
     *
     * @param actNumber account number
     * @return list of issued card types
     */
    @Override
    public List<String> issuedCardTypeByAct(long actNumber) {
        List<String> cardTypeList = new ArrayList<>();
        String q = "SELECT card_type FROM cards WHERE account_number = ? AND card_status <> ?";

        try (PreparedStatement statement = connection.prepareStatement(q)) {
            statement.setLong(1, actNumber);
            statement.setString(2, CARD_STATUS_EXPIRED);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    cardTypeList.add(rs.getString("card_type"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching card types for accountNumber={}", actNumber, e);
        }
        return cardTypeList;
    }

    /**
     * Creates a new account.
     *
     * @param account Account object
     * @return generated account ID or -1 if failed
     * @throws SQLException database error
     */
    @Override
    public long create(Account account) throws SQLException {

        String sql = """
            INSERT INTO accounts
            (user_id, branch_id, account_number, account_type, balance, nominee_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, account.getUserId());
            ps.setLong(2, account.getBranchId());
            ps.setLong(3, account.getAccountNumber());
            ps.setString(4, account.getAccountType().toString());
            ps.setDouble(5, account.getBalance());
            ps.setLong(6, account.getNominee_id());

            int rows = ps.executeUpdate();
            logger.info("Account create executed, rows affected={}", rows);

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return -1;
    }

    /**
     * Finds account by account number.
     *
     * @param accountNo account number
     * @return Account or null
     * @throws SQLException database error
     */
    @Override
    public Account findByNumber(long accountNo) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowtoAccount(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds user ID by account number.
     *
     * @param accountNo account number
     * @return user ID or null
     * @throws SQLException database error
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

    /**
     * Finds account by account ID.
     *
     * @param accountId account ID
     * @return Account or null
     * @throws SQLException database error
     */
    @Override
    public Account findById(long accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowtoAccount(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds account by user ID and account type.
     *
     * @param userId user ID
     * @param type account type
     * @return Account or null
     * @throws SQLException database error
     */
    public Account findByType(long userId, String type) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_type = ? AND user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowtoAccount(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds all accounts for a user.
     *
     * @param userId user ID
     * @return list of accounts
     * @throws SQLException database error
     */
    @Override
    public List<Account> findByUserId(long userId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ?";

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

    /**
     * Finds account numbers for a user.
     *
     * @param userId user ID
     * @return list of account numbers
     * @throws SQLException database error
     */
    @Override
    public List<Long> findByNumberUserId(long userId) throws SQLException {
        List<Long> accounts = new ArrayList<>();
        String sql = "SELECT account_number FROM accounts WHERE user_id = ?";

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

    /**
     * Finds all accounts.
     *
     * @return list of accounts
     * @throws SQLException database error
     */
    @Override
    public List<Account> findAll() throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowtoAccount(rs));
            }
        }
        return list;
    }

    /**
     * Updates account type, status, and currency.
     *
     * @param accountId account ID
     * @param accountType account type
     * @param status status
     * @param currency currency
     * @return true if updated
     * @throws SQLException database error
     */
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

    /**
     * Updates account balance.
     *
     * @param accountId account ID
     * @param balance new balance
     * @return true if updated
     * @throws SQLException database error
     */
    @Override
    public boolean updateBalance(long accountId, double balance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, balance);
            ps.setLong(2, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an account.
     *
     * @param accountId account ID
     * @return true if deleted
     * @throws SQLException database error
     */
    @Override
    public boolean delete(long accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Returns the highest account number.
     *
     * @return highest account number
     */
    public Long getHighestAccountNo() {
        String query = "SELECT account_number FROM accounts ORDER BY account_number DESC LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong(ACCOUNT_NO) : 0L;

        } catch (SQLException e) {
            logger.error("Error fetching highest account number", e);
            return -1L;
        }
    }

    /**
     * Maps ResultSet to Account object.
     *
     * @param rs ResultSet
     * @return Account
     * @throws SQLException database error
     */
    private Account mapRowtoAccount(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong(ACCOUNT_ID),
                rs.getLong(USER_ID),
                rs.getLong(BRANCH_ID),
                rs.getLong(NOMINEE_ID),
                rs.getLong(ACCOUNT_NO),
                AccountType.valueOf(rs.getString(ACCOUNT_TYPE).toUpperCase()),
                rs.getBigDecimal(BALANCE).doubleValue(),
                rs.getString(CURRENCY),
                AccountStatus.valueOf(rs.getString(STATUS).toUpperCase()),
                rs.getTimestamp(OPENED_AT).toLocalDateTime(),
                rs.getTimestamp(UPDATED_AT).toLocalDateTime()
        );
    }
}
