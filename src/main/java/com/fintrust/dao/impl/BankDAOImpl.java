package com.fintrust.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.fintrust.dao.BankDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Bank;

public class BankDAOImpl implements BankDAO{

	private Connection connection = null;
	
	public BankDAOImpl() {
		connection = DBConnection.getConnection();
	}
	
	@Override
	public long create(Bank bank) throws SQLException {
//		Statement st = connection.createStatement();
//		String query = """
//				INSERT INTO banks (bank_name, branch_name, ifsc_code, support_email, support_phone)
//				 VALUES ('Fintrust', 'Internet Banking', 'FINT0000000', 'support@fintrust.com', '1800-123-456');
//				""";
//		st.executeUpdate(query);
		return 0;
	}

	@Override
	public Bank findById(long bankId) throws SQLException {
		String query = "Select * from banks where bank_id = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query);) {
			pstmt.setLong(1, bankId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Bank bank = new Bank();
				bank.setBankId(rs.getLong("bank_id"));
				bank.setBankName(rs.getString("bank_name"));
				bank.setBranchName(rs.getString("branch_name"));
				bank.setIfscCode(rs.getString("ifsc_code"));
				bank.setCreatedAt(rs.getTimestamp("created_at"));
				return bank;
			}
			
		}
		return null;
	}

	@Override
	public List<Bank> findAll() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update(Bank bank) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(long bankId) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
