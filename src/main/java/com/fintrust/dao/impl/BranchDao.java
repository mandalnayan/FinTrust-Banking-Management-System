package com.fintrust.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.fintrust.dao.BranchDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Branch;

public class BranchDao implements BranchDAO {

	private Connection connection = null;

	public BranchDao() {
		connection = DBConnection.getConnection();
	}

	@Override
	public long create(Branch branch) throws SQLException {
//		Statement st = connection.createStatement();
//		String query = """
//				INSERT INTO banks (bank_name, branch_name, ifsc_code, support_email, support_phone)
//				 VALUES ('Fintrust', 'Internet Banking', 'FINT0000000', 'support@fintrust.com', '1800-123-456');
//				""";
//		st.executeUpdate(query);
		return 0;
	}

	@Override
	public Branch findById(long BranchId) throws SQLException {
		String query = "Select * from branches where branch_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query);) {
			pstmt.setLong(1, BranchId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Branch branch = new Branch();
				branch.setBranchId(rs.getLong("branch_id"));
				branch.setBranchName(rs.getString("branch_name"));
				branch.setIfscCode(rs.getString("ifsc_code"));
				branch.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
				return branch;
			}
		}
		return null;
	}
	
	@Override
	public Branch findByBranchName(String branchName) throws SQLException {
		String query = "Select * from branches where branch_name = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query);) {
			pstmt.setString(1, branchName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Branch branch = new Branch();
				branch.setBranchId(rs.getLong("branch_id"));
				branch.setBranchName(rs.getString("branch_name"));
				branch.setIfscCode(rs.getString("ifsc_code"));
				branch.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
				return branch;
			}
		}
		return null;
	}

	@Override
	public List<Branch> findAll() throws SQLException {
		List<Branch> allBranch = new ArrayList<>();
		String query = "Select * from branches";
		try (PreparedStatement pstmt = connection.prepareStatement(query);) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Branch branch = new Branch();
				branch.setBranchId(rs.getLong("branch_id"));
				branch.setBranchName(rs.getString("branch_name"));
				branch.setIfscCode(rs.getString("ifsc_code"));
				branch.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
				allBranch.add(branch);
			}
		}
		return allBranch;
	}

	@Override
	public boolean update(Branch branch) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(long branchId) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	

}
