package com.fintrust.dao.impl;



import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.fintrust.dao.CardsDAO;

/**
 * JDBC implementation of CardsDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking-grade standards.
 */
public class CardsDAOImpl implements CardsDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public CardsDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long userId, Long accountId, String cardNumberMasked,
                       String cardBin, String cardType, String provider,
                       Date expiryDate, String status) throws SQLException {

        String sql = """
            INSERT INTO cards
            (user_id, account_id, card_number_masked, card_bin, card_type, provider, expiry_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            if (accountId != null) ps.setLong(2, accountId); else ps.setNull(2, Types.BIGINT);
            ps.setString(3, cardNumberMasked);
            ps.setString(4, cardBin);
            ps.setString(5, cardType.toLowerCase());
            ps.setString(6, provider != null ? provider.toLowerCase() : null);
            ps.setDate(7, expiryDate);
            ps.setString(8, status != null ? status.toLowerCase() : "active");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long cardId) throws SQLException {
        String sql = "SELECT * FROM cards WHERE card_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM cards WHERE user_id = ? ORDER BY issued_date DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM cards ORDER BY issued_date DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }

        return list;
    }

    @Override
    public boolean update(long cardId, String status, Date expiryDate) throws SQLException {
        String sql = "UPDATE cards SET status = ?, expiry_date = ? WHERE card_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toLowerCase());
            ps.setDate(2, expiryDate);
            ps.setLong(3, cardId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long cardId) throws SQLException {
        String sql = "DELETE FROM cards WHERE card_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the card record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("card_id", rs.getLong("card_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("account_id", rs.getObject("account_id"));
        map.put("card_number_masked", rs.getString("card_number_masked"));
        map.put("card_bin", rs.getString("card_bin"));
        map.put("card_type", rs.getString("card_type"));
        map.put("provider", rs.getString("provider"));
        map.put("expiry_date", rs.getDate("expiry_date"));
        map.put("status", rs.getString("status"));
        map.put("issued_at", rs.getTimestamp("issued_date"));
        return map;
    }
}

