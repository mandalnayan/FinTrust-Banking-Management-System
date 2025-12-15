package com.fintrust.service;

import java.sql.Connection;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.impl.FundTransferDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction.TransactionStatus;

public class FundTransferService {

	Connection connection = DBConnection.getConnection();
	private final FundTransferDAO dao = new FundTransferDAO(connection);

	public boolean transferFunds(Long fromAcc, Long toAcc, String ifscCode, double amount) {

		Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
		if (userId == null) {
			throw new IllegalStateException("User not logged in");
		}

		try {

			validateBalance(fromAcc, amount);
			validateReceiver(toAcc, ifscCode);

			dao.debit(fromAcc, amount);
			dao.credit(toAcc, amount);

			dao.insertTransaction(userId, fromAcc, toAcc, amount,
					TransactionStatus.COMPLETED.name().toLowerCase());

			connection.commit();
			return true;

		} catch (Exception e) {
			rollbackQuietly();

			try {
				if (connection != null) {
					dao.insertTransaction(userId, fromAcc, toAcc, amount,
							TransactionStatus.FAILED.name().toLowerCase());
				}
			} catch (Exception ignore) {
			}

			throw new RuntimeException("Fund transfer failed", e);

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
