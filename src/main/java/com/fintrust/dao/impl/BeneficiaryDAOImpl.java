package com.fintrust.dao.impl;

import java.sql.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fintrust.dao.BeneficiaryDAO;
import com.fintrust.model.Beneficiary;

/**
 * JDBC implementation of {@link BeneficiaryDAO}.
 * <p>
 * This DAO handles all beneficiary-related CRUD operations using
 * {@link PreparedStatement} to ensure SQL injection safety.
 * <p>
 * Designed for banking applications with proper logging for auditing
 * and troubleshooting.
 */
public class BeneficiaryDAOImpl implements BeneficiaryDAO {

    private static final Logger logger = LogManager.getLogger(BeneficiaryDAOImpl.class);

    /** JDBC connection managed externally */
    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed by the service layer
     */
    public BeneficiaryDAOImpl(Connection connection) {
        this.connection = connection;
        logger.debug("BeneficiaryDAOImpl initialized with provided JDBC connection.");
    }

    /**
     * Creates a new beneficiary record in the database.
     *
     * @param beneficiary beneficiary details
     * @return generated beneficiary ID, or -1 if creation fails
     * @throws SQLException if database operation fails
     */
    @Override
    public long create(Beneficiary beneficiary) throws SQLException {

        String sql = """
            INSERT INTO beneficiaries (user_id, name, account_number, bank_name, ifsc_code)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, beneficiary.getUserId());
            ps.setString(2, beneficiary.getName());
            ps.setLong(3, beneficiary.getAccountNumber());
            ps.setString(4, beneficiary.getBankName());
            ps.setString(5, beneficiary.getIfscCode());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    logger.info(
                        "Beneficiary created successfully. beneficiaryId={}, userId={}, accountNumber={}",
                        id, beneficiary.getUserId(), beneficiary.getAccountNumber()
                    );
                    return id;
                }
            }
        }

        logger.warn(
            "Failed to create beneficiary. userId={}, accountNumber={}",
            beneficiary.getUserId(), beneficiary.getAccountNumber()
        );
        return -1;
    }

    /**
     * Fetches a beneficiary by beneficiary ID.
     *
     * @param beneficiaryId beneficiary ID
     * @return {@link Beneficiary} object or {@code null} if not found
     * @throws SQLException if database access fails
     */
    @Override
    public Beneficiary findById(long beneficiaryId) throws SQLException {
        String sql = "SELECT * FROM beneficiaries WHERE beneficiary_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, beneficiaryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Beneficiary found for beneficiaryId={}", beneficiaryId);
                    return mapRowtoModel(rs);
                }
            }
        }

        logger.debug("No beneficiary found for beneficiaryId={}", beneficiaryId);
        return null;
    }

    /**
     * Fetches all beneficiaries for a specific user.
     *
     * @param userId user ID
     * @return list of beneficiaries
     * @throws SQLException if database access fails
     */
    @Override
    public List<Beneficiary> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM beneficiaries WHERE user_id = ?";
        List<Beneficiary> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowtoModel(rs));
                }
            }
        }

        logger.info("Fetched {} beneficiaries for userId={}", list.size(), userId);
        return list;
    }

    /**
     * Fetches all beneficiaries in the system.
     *
     * @return list of all beneficiaries
     * @throws SQLException if database access fails
     */
    @Override
    public List<Beneficiary> findAll() throws SQLException {
        String sql = "SELECT * FROM beneficiaries ORDER BY beneficiary_id ASC";
        List<Beneficiary> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowtoModel(rs));
            }
        }

        logger.info("Fetched total {} beneficiaries from database.", list.size());
        return list;
    }

    /**
     * Updates beneficiary details.
     *
     * @param beneficiaryId beneficiary ID
     * @param name updated name
     * @param accountNumber updated account number
     * @param bankName updated bank name
     * @param ifscCode updated IFSC code
     * @return {@code true} if update successful
     * @throws SQLException if database access fails
     */
    @Override
    public boolean update(long beneficiaryId, String name, String accountNumber,
                          String bankName, String ifscCode) throws SQLException {

        String sql = """
            UPDATE beneficiaries SET
                name = ?, account_number = ?, bank_name = ?, ifsc_code = ?
            WHERE beneficiary_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, accountNumber);
            ps.setString(3, bankName);
            ps.setString(4, ifscCode);
            ps.setLong(5, beneficiaryId);

            boolean updated = ps.executeUpdate() > 0;
            if (updated) {
                logger.info("Beneficiary updated successfully. beneficiaryId={}", beneficiaryId);
            } else {
                logger.warn("No beneficiary updated. beneficiaryId={}", beneficiaryId);
            }
            return updated;
        }
    }

    /**
     * Deletes a beneficiary by ID.
     *
     * @param beneficiaryId beneficiary ID
     * @return {@code true} if deletion successful
     * @throws SQLException if database access fails
     */
    @Override
    public boolean delete(long beneficiaryId) throws SQLException {
        String sql = "DELETE FROM beneficiaries WHERE beneficiary_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, beneficiaryId);
            boolean deleted = ps.executeUpdate() > 0;

            if (deleted) {
                logger.info("Beneficiary deleted successfully. beneficiaryId={}", beneficiaryId);
            } else {
                logger.warn("No beneficiary deleted. beneficiaryId={}", beneficiaryId);
            }
            return deleted;
        }
    }

    /**
     * Maps a ResultSet row into a Map representation.
     * <p>
     * This method is currently unused and kept for future extensibility.
     *
     * @param rs ResultSet positioned at the row
     * @return map containing column-value pairs
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("beneficiary_id", rs.getLong("beneficiary_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("name", rs.getString("name"));
        map.put("account_number", rs.getString("account_number"));
        map.put("bank_name", rs.getString("bank_name"));
        map.put("ifsc_code", rs.getString("ifsc_code"));
        map.put("added_at", rs.getTimestamp("added_at"));
        return map;
    }

    /**
     * Maps a ResultSet row to a {@link Beneficiary} domain model.
     *
     * @param rs ResultSet positioned at the row
     * @return populated {@link Beneficiary} object
     * @throws SQLException if column access fails
     */
    private Beneficiary mapRowtoModel(ResultSet rs) throws SQLException {
        Beneficiary b = new Beneficiary();

        b.setBeneficiaryId(rs.getLong("beneficiary_id"));
        b.setUserId(rs.getLong("user_id"));
        b.setName(rs.getString("name"));
        b.setAccountNumber(rs.getLong("account_number"));
        b.setBankName(rs.getString("bank_name"));
        b.setIfscCode(rs.getString("ifsc_code"));
        b.setAddedAt(rs.getTimestamp("added_at"));

        return b;
    }
}
