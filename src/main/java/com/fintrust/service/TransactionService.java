package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.fintrust.dao.TransactionsDAO;
import com.fintrust.dao.impl.TransactionsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.util.NotificationUtil;

public class TransactionService {
	   private TransactionsDAO transactionDao;
	   	Connection connection = DBConnection.getConnection();
	    public TransactionService() {
	        this.transactionDao = new TransactionsDAOImpl(connection);
	    }

	    
	    public Double getTodayTotalTransaction() {
	        try {
				return transactionDao.getTodayTotalTransactionAmount();
			} catch (SQLException e) {
				NotificationUtil.showInstant("error", "Failed to load today transactions. \n" + e.getMessage());
				e.printStackTrace();
			}
	       return 0d;
	    }
}
