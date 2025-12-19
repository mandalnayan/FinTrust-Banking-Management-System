package com.fintrust.service;

import java.sql.Connection;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.impl.FundTransferDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction.TransactionStatus;
import com.fintrust.util.NotificationUtil;

public class FundTransferService {

	Connection connection = DBConnection.getConnection();
	private final FundTransferDAO dao = new FundTransferDAO(connection);

	public boolean transferFunds(Long fromAcc, Long toAcc, String ifscCode, double amount) {

		Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
		if (userId == null) {
			throw new IllegalStateException("User not logged in");
		}

		try {
			// Either all operation must happened or nore
			connection.setAutoCommit(false);
			
			validateBalance(fromAcc, amount);
			validateReceiver(toAcc, ifscCode);

			if (!dao.isAccountActive(fromAcc)) throw new IllegalArgumentException("Sender account is not active. Please contact to bank!");
			if (!dao.isAccountActive(toAcc)) throw new IllegalArgumentException("Receiver bank server is down! or account is not active");
			
			dao.debit(fromAcc, amount);
			dao.credit(toAcc, amount);

			dao.insertTransaction(userId, fromAcc, toAcc, amount, "credit",
					TransactionStatus.COMPLETED.name().toLowerCase());
			
			dao.insertTransaction(userId, fromAcc, toAcc, amount, "debit",
					TransactionStatus.COMPLETED.name().toLowerCase());

			connection.commit();
			return true;

		} catch (Exception e) {
			rollbackQuietly();
			NotificationUtil.showInstant("error", "Fund transfer failed! " + e.getMessage());
			try {
				if (connection != null) {
					dao.insertTransaction(userId, fromAcc, toAcc, amount,"debit",
							TransactionStatus.FAILED.name().toLowerCase());
				}
			} catch (Exception ignore) {
			}

			return false;

		} finally {
			closeQuietly();
		}
	}

	private void validateBalance(Long fromAcc, double amount) throws Exception {

		double balance = dao.getAccountBalance(fromAcc);
		if (balance < amount) {
			throw new IllegalArgumentException("Insufficient balance");
		}
	}

	private void validateReceiver(Long toAcc, String ifsc) throws Exception {

		long branchId = dao.getBranchId(toAcc);
		boolean valid = dao.validateIFSC(branchId, ifsc);

		if (!valid) {
			throw new IllegalArgumentException("Invalid receiver IFSC");
		}
	}

	private void rollbackQuietly() {
		try {
			if (connection != null)
				connection.rollback();
		} catch (Exception ignored) {
		}
	}

	private void closeQuietly() {
		try {
			if (connection != null)
				connection.close();
		} catch (Exception ignored) {
		}
	}
}
