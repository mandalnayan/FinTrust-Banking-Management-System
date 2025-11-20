package com.fintrust.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import com.fintrust.db.DBConnection;
import com.fintrust.model.Account;
import com.fintrust.model.Account.AccountStatus;
import com.fintrust.model.Account.AccountType;
import com.fintrust.model.Account.ModeOfOperation;

public class AccountDaoImp implements AccountDao {

	@Override
	public boolean isSameAccount(long customer_id, String accountType) {
		String q = "select * from account where customer_id = ? and account_type = ?";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(q)) {

			statement.setLong(1, customer_id);
			statement.setString(2, accountType);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				Messagebox.show("Account already exist");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
	}

	public boolean createSchema() {
		String query = """
					CREATE TABLE IF NOT EXISTS customer (
					customer_id BIGINT PRIMARY KEY,,
					name VARCHAR(100) NOT NULL,
					email VARCHAR(150) UNIQUE NOT NULL,
					phone VARCHAR(20),
					created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
				);
				""";
		try {
			Statement statement = DBConnection.getConnection().createStatement();
			statement.executeUpdate(query);
			System.out.println("customer table created");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
	}

	public Account getDefaultAccount() {

		try (Connection con = DBConnection.getConnection()) {
			if (con != null) {
				Statement statement = con.createStatement();
				ResultSet rs = statement.executeQuery("Select * from account limit 1");
				// System.out.println("account data fetched!!");
				if (rs.next()) {
					Account account = new Account();
					account.setAccount_no(rs.getLong(1));
					account.setBalance(rs.getDouble(2));
					account.setAccount_type(AccountType.valueOf(rs.getString(3)));
					account.setAccount_status(AccountStatus.valueOf(rs.getString(4)));
					account.setBranch_Name(rs.getString(5));
					account.setMode_of_operation(ModeOfOperation.valueOf(rs.getString(6)));
					account.setNominee_id(rs.getLong(7));
					account.setCustomer_id(rs.getLong(8));
					account.setCreated_at(rs.getTimestamp(9).toLocalDateTime());
					return account;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return null;
	}

	private boolean createAccountSchema() {
		String query = """
					CREATE TABLE IF NOT EXISTS account (
				    account_no BIGINT PRIMARY KEY,
				    balance DOUBLE NOT NULL CHECK (balance >= 0),
				    account_type ENUM('SAVING', 'CURRENT', 'SALARY') NOT NULL,
				    account_status ENUM('ACTIVE', 'INACTIVE', 'CLOSED') DEFAULT 'ACTIVE',
				    branch_name VARCHAR(100) NOT NULL,
				    mode_of_operation ENUM('SELF', 'JOINT', 'EITHER_OR_SURVIVOR') NOT NULL,

				    nominee_id BIGINT,
				    FOREIGN KEY (nominee_id) REFERENCES nominee(nominee_id),

				    customer_id BIGINT NOT NULL,
				    FOREIGN KEY (customer_id) REFERENCES users(id),

				    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
				);

				""";

		try (Connection con = DBConnection.getConnection()) {
			if (con != null) {
				Statement statement = con.createStatement();
				statement.executeUpdate(query);
				System.out.println("account table created");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean createAccount(Account account) {
		String query = "insert into account values(?,?,?,?,?,?,?,?,?)";
		try (Connection con = DBConnection.getConnection();) {
			if (con != null) {
				PreparedStatement statement = con.prepareStatement(query);
				statement.setLong(1, account.getAccount_no());
				statement.setDouble(2, account.getBalance());
				statement.setString(3, account.getAccount_type().toString());
				statement.setString(4, account.getAccount_status().toString());

				statement.setString(5, account.getBranch_Name());
				statement.setString(6, account.getMode_of_operation().toString());
				statement.setLong(7, account.getNominee_id());
				statement.setLong(8, account.getCustomer_id());
				statement.setTimestamp(9, Timestamp.valueOf(account.getCreated_at()));

				if (statement.executeUpdate() > 0) {
					System.out.println("Account created successfully");
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
	}

	@Override
	public Account getAccountByNo(long accountNo) {
		String query = "select * from account where account_no = ?";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
			statement.setLong(1, accountNo);

			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				Account account = new Account();
				account.setAccount_no(rs.getLong(1));
				account.setBalance(rs.getDouble(2));
				account.setAccount_type(AccountType.valueOf(rs.getString(3)));
				account.setAccount_status(AccountStatus.valueOf(rs.getString(4)));
				account.setBranch_Name(rs.getString(5));
				account.setMode_of_operation(ModeOfOperation.valueOf(rs.getString(6)));
				account.setNominee_id(rs.getLong(7));
				account.setCustomer_id(rs.getLong(8));
				account.setCreated_at(rs.getTimestamp(9).toLocalDateTime());
				return account;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Account> getAllAccounts() {
		List<Account> accounts = new ArrayList<>();
		String query = "select * from account";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Account account = new Account();
				account.setAccount_no(rs.getLong(1));
				account.setBalance(rs.getDouble(2));
				account.setAccount_type(AccountType.valueOf(rs.getString(3)));
				account.setAccount_status(AccountStatus.valueOf(rs.getString(4)));
				account.setBranch_Name(rs.getString(5));
				account.setMode_of_operation(ModeOfOperation.valueOf(rs.getString(6)));
				account.setNominee_id(rs.getLong(7));
				account.setCustomer_id(rs.getLong(8));
				account.setCreated_at(rs.getTimestamp(9).toLocalDateTime());
				accounts.add(account);
			}
			return accounts;
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return accounts;
	}

	@Override
	public List<Account> getAllAccounts(long customerId) {
		List<Account> accounts = new ArrayList<>();
		String query = "select * from account where customer_id = ?";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
			statement.setLong(1, customerId);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Account account = new Account();
				account.setAccount_no(rs.getLong(1));
				account.setBalance(rs.getDouble(2));
				account.setAccount_type(AccountType.valueOf(rs.getString(3)));
				account.setAccount_status(AccountStatus.valueOf(rs.getString(4)));
				account.setBranch_Name(rs.getString(5));
				account.setMode_of_operation(ModeOfOperation.valueOf(rs.getString(6)));
				account.setNominee_id(rs.getLong(7));
				account.setCustomer_id(rs.getLong(8));
				account.setCreated_at(rs.getTimestamp(9).toLocalDateTime());
				accounts.add(account);
			}
			return accounts;
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return accounts;
	}

	@Override
	public boolean updateAccount(Account account) {
		String sql = "UPDATE account SET balance=?, account_type=?, branch_name=?, mode_of_operation =?,nominee_id=?, WHERE account_no=?";
		try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
			ps.setDouble(1, account.getBalance());
			ps.setString(2, account.getAccount_type().toString());
			ps.setString(3, account.getBranch_Name());
			ps.setString(4, account.getMode_of_operation().toString());
			ps.setLong(5, account.getNominee_id());
			ps.setLong(6, account.getAccount_no());
			if (ps.executeUpdate() > 0) {
				System.out.println("Account updated successfully");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean updateBalance(long accountNo, double newBalance) {
		String query = "UPDATE account SET balance = ? WHERE account_no = ?";

		try (Connection conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false); // Start transaction

			try (PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setDouble(1, newBalance);
				statement.setLong(2, accountNo);

				int rows = statement.executeUpdate();
				if (rows > 0) {
					conn.commit(); // Commit if successful
					Messagebox.show("Balance updated successfully");
					return true;
				} else {
					Messagebox.show("No account found with account number: ");
				}
			} catch (SQLException e) {
				conn.rollback(); // Rollback if statement fails
				e.printStackTrace();
				Messagebox.show("Error while updating balance:\n" + e.getMessage());
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show("Database connection error:\n" + e.getMessage());
		}
		return false;
	}

	@Override
	public boolean updateAccountStatus(long accountNo, String status) {
		String query = "update account set account_status = ? where account_no = ?";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
			statement.setString(1, status);
			statement.setLong(2, accountNo);

			if (statement.executeUpdate() > 0) {
				System.out.println("Account Status successfully upadated");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean deleteAccount(long accountNo) {
		String query = "delete from account where account_no = ?";
		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query)) {
			statement.setLong(1, accountNo);

			if (statement.executeUpdate() > 0) {
				System.out.println("Account deleted successfully");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
	}

	public long getHighestAccountNo() {
		String query = "SELECT account_no FROM account ORDER BY account_no DESC LIMIT 1";

		try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			if (resultSet.next()) {
				long highestAccountNo = resultSet.getLong("account_no");
				// System.out.println("Highest Account No: " + highestAccountNo);
				return highestAccountNo;
			} else {
				return 10001000;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return 10001000; // return 10001000 if no record found or error occurred
	}

	@Override
	public List<Long> getAccountsByUserId(Long userId) {
		String sql = "select account_no from account where customer_id=?;";
		List<Long> accountList = new ArrayList<>();
		try (Connection con = DBConnection.getConnection(); PreparedStatement pStatement = con.prepareStatement(sql);) {

			Long customer_id = userId != null ? userId : (Long) Sessions.getCurrent().getAttribute("customer_id"); // take
																																																// session
			if (customer_id != null) {
				pStatement.setLong(1, customer_id);

				ResultSet resultSet = pStatement.executeQuery();

				while (resultSet.next()) {
					accountList.add(resultSet.getLong("account_no"));
				}
				return accountList;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;

	}
}
