package com.fintrust.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fintrust.db.DBConnection;
import com.fintrust.model_copy.BeneficiaryModel;

/**
 * DAO class responsible for beneficiary-related database operations.
 * <p>
 * Provides methods to add a beneficiary and retrieve beneficiaries
 * associated with a specific user. Designed for use in a banking application
 * with proper logging for audit and troubleshooting purposes.
 */
public class BeneficiaryDAO {

    private static final Logger logger = LogManager.getLogger(BeneficiaryDAO.class);

    /**
     * Adds a new beneficiary record to the database.
     *
     * @param b {@link BeneficiaryModel} containing beneficiary details
     * @return {@code true} if the beneficiary is successfully added, {@code false} otherwise
     */
    public static boolean addBeneficiary(BeneficiaryModel b) {
        String sql = "INSERT INTO beneficiary(user_id, name, account_number, bank_name, ifsc_code) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, b.getUserId());
            ps.setString(2, b.getName());
            ps.setString(3, b.getAccountNumber());
            ps.setString(4, b.getBankName());
            ps.setString(5, b.getIfscCode());

            boolean result = ps.executeUpdate() > 0;
            if (result) {
                logger.info("Beneficiary added successfully for userId: {}, account: {}", 
                        b.getUserId(), b.getAccountNumber());
            } else {
                logger.warn("Failed to add beneficiary for userId: {}, account: {}", 
                        b.getUserId(), b.getAccountNumber());
            }
            return result;

        } catch (Exception e) {
            logger.error("Error while adding beneficiary for userId: {}, account: {}", 
                    b.getUserId(), b.getAccountNumber(), e);
            return false;
        }
    }

    /**
     * Retrieves all beneficiaries associated with a given user ID.
     *
     * @param userId the user ID whose beneficiaries are to be fetched
     * @return list of {@link BeneficiaryModel}; empty list if none found
     */
    public static List<BeneficiaryModel> getBeneficiariesByUserId(Long userId) {
        List<BeneficiaryModel> list = new ArrayList<>();
        String sql = "SELECT * FROM beneficiary WHERE user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

            logger.info("Fetched {} beneficiaries for userId: {}", list.size(), userId);

        } catch (Exception e) {
            logger.error("Error while fetching beneficiaries for userId: {}", userId, e);
        }
        return list;
    }

    /**
     * Creates beneficiary table schema.
     * <p>
     * This method is currently unused and intended for reference or
     * database migration purposes only.
     */
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
                    FOREIGN KEY (account_number) REFERENCES accounts(account_number),
                    FOREIGN KEY (bank_name) REFERENCES banks(bank_name),
                    FOREIGN KEY (ifsc_code) REFERENCES banks(ifsc_code)
                )
                """;
    }
}
