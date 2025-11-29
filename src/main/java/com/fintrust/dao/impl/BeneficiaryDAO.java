package com.fintrust.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.fintrust.db.DBConnection;
import com.fintrust.model_copy.BeneficiaryModel;

public class BeneficiaryDAO {

	public static boolean addBeneficiary(BeneficiaryModel b) {
		String sql = "INSERT INTO beneficiary(user_id, name, account_number, bank_name, ifsc_code) VALUES (?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, b.getUserId());
			ps.setString(2, b.getName());
			ps.setString(3, b.getAccountNumber());
			ps.setString(4, b.getBankName());
			ps.setString(5, b.getIfscCode());

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static List<BeneficiaryModel> getBeneficiariesByUserId(Long userId) {
		List<BeneficiaryModel> list = new ArrayList<>();
		String sql = "SELECT * FROM beneficiary WHERE user_id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, userId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				BeneficiaryModel b = new BeneficiaryModel();
				b.setBeneficiaryId(rs.getInt("beneficiary_id"));
				b.setUserId(rs.getInt("user_id"));
				b.setName(rs.getString("name"));
				b.setAccountNumber(rs.getString("account_number"));
				b.setBankName(rs.getString("bank_name"));
				b.setIfscCode(rs.getString("ifsc_code"));
				list.add(b);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private void createSchema() {
		String query = """
				    			CREATE TABLE beneficiary (
				    beneficiary_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
				    user_id BIGINT UNSIGNED NOT NULL,
				    account_number VARCHAR(20) NOT NULL,
				    bank_name VARCHAR(100) NOT NULL,
				    ifsc_code VARCHAR(20) NOT NULL,
				    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				    PRIMARY KEY (beneficiary_id),
				    FOREIGN KEY (user_id) REFERENCES users(user_id),
				    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
				    FOREIGN KEY (bank_name) REFERENCES banks(bank_name),
				    FOREIGN KEY (ifsc_code) REFERENCES banks(ifsc_code),
				)

				    			""";
	}
}
