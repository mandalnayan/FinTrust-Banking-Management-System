package com.fintrust.dao.impl;



import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.User;
import com.fintrust.model.UserDetails;

/**
 * JDBC implementation of UserDetailsDAO for banking systems.
 * <p>
 * Implements all CRUD operations securely using PreparedStatements
 * and follows banking-grade standards.
 */
public class UserDetailsDAOImpl implements UserDetailsDAO {

    private final Connection connection;

    /**
     * Constructor injection of JDBC connection.
     *
     * @param connection managed externally
     */
    public UserDetailsDAOImpl() {
        this.connection = DBConnection.getConnection();
    }

    @Override
    public long create(long userId, String gender, Date dob,
                       String aadhaarMask, String panMask,
                       String country, String state, String district,
                       String city, String pincode) throws SQLException {

        String sql = """
            INSERT INTO user_details
            (user_id, gender, dob, aadhaar_masked, pan_masked, country, state, district, city, pincode)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, gender != null ? gender.toLowerCase() : null);
            ps.setDate(3, dob);
            ps.setString(4, aadhaarMask);
            ps.setString(5, panMask);
            ps.setString(6, country);
            ps.setString(7, state);
            ps.setString(8, district);
            ps.setString(9, city);
            ps.setString(10, pincode);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }
//
//    public boolean insert() {
//    	"""
//    		INSERT INTO user_details (
//    ->     user_id,
//    ->     gender,
//    ->     dob,
//    ->     aadhaar_masked,
//    ->     pan_masked,
//    ->     country,
//    ->     state,
//    ->     district,
//    ->     city,
//    ->     pincode,
//    ->     primary_account_id
//    -> ) VALUES (
//    ->     1,                        -- existing user_id
//    ->     'male',
//    ->     '1995-04-25',
//    ->     'XXXX-XXXX-1234',
//    ->     'XXXXX1234X',
//    ->     'India',
//    ->     'Maharashtra',
//    ->     'Pune',
//    ->     'Pune',
//    ->     '411001',
//    ->     3                     -- existing account_id
//    -> );
//
//    		""";
//    		return false;
//    }
    
    @Override
    public UserDetails findById(long detailsId) throws SQLException {
        String sql = "SELECT * FROM user_details WHERE details_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, detailsId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowWithUserDetails(rs);
            }
        }
        return null;
    }

    /**
     * Find user complete details by joining User and user_details table
     */
    @Override
    public UserDetails findByUserId(long userId) throws SQLException {
		String sql = """
				     SELECT ud.*, us.*
				FROM user_details ud
				INNER JOIN users us ON ud.user_id = us.user_id
				WHERE ud.user_id = ?;

				        		""";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowWithUserDetails(rs);
            }
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM user_details ORDER BY details_id ASC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public boolean update(long detailsId, String gender, Date dob,
                          String aadhaarMask, String panMask,
                          String country, String state, String district,
                          String city, String pincode) throws SQLException {

        String sql = """
            UPDATE user_details SET
                gender = ?, dob = ?, aadhaar_masked = ?, pan_masked = ?,
                country = ?, state = ?, district = ?, city = ?, pincode = ?
            WHERE details_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, gender != null ? gender.toLowerCase() : null);
            ps.setDate(2, dob);
            ps.setString(3, aadhaarMask);
            ps.setString(4, panMask);
            ps.setString(5, country);
            ps.setString(6, state);
            ps.setString(7, district);
            ps.setString(8, city);
            ps.setString(9, pincode);
            ps.setLong(10, detailsId);

            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Find PrimaryAccount of user
     * @param userId
     * @throws SQLException 
     */
    public Long findPrimaryAccount(Long userId) throws SQLException {
        String sql = "Select * from user_details WHERE user_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
        	ps.setLong(1, userId);
        	   ResultSet rs = ps.executeQuery();
        	   if (rs.next()) {
        		   return rs.getLong("primary_account_id");
        	   }
        }  
        return -1l;
    }
    
    /**
     * Update PrimaryAccount of user
     * @param userId
     * @param accountId
     * @throws SQLException 
     */
    public boolean updatePrimaryAccount(Long userId, Long accountId) throws SQLException {
        String sql = "UPDATE user_details SET primary_account_id = ? WHERE user_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }       
    }

    @Override
    public boolean delete(long detailsId) throws SQLException {
        String sql = "DELETE FROM user_details WHERE details_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, detailsId);
            return ps.executeUpdate() > 0;
        }
    }

    private UserDetails mapRowWithUserDetails(ResultSet rs) throws SQLException {
    	 UserDetails ud = new UserDetails();
    	 // Fetching user data
    	 User user = new User(
				 rs.getLong("user_id"),
				 rs.getString("full_name"),
				 rs.getString("email"),
				 rs.getString("phone"),
				 rs.getString("role"),
				 rs.getString("status"),
				 rs.getTimestamp("created_at"),
				 rs.getTimestamp("updated_at")
				 );
    	 ud.setUser(user);
         ud.setDetailsId(rs.getLong("details_id"));         
         ud.setGender(rs.getString("gender"));
         ud.setDob(rs.getDate("dob").toLocalDate());
         ud.setAadhaarMasked(rs.getString("aadhaar_masked"));
         ud.setPanMasked(rs.getString("pan_masked"));
         ud.setCountry(rs.getString("country"));
         ud.setState(rs.getString("state"));
         ud.setDistrict(rs.getString("district"));
         ud.setCity(rs.getString("city"));
         ud.setPincode(rs.getString("pincode"));
         ud.setPrimaryAccountId(rs.getLong("primary_account_id"));
         return ud;
    }
    /**
     * Maps a ResultSet row into a Map representing the user_details record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("details_id", rs.getLong("details_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("gender", rs.getString("gender"));
        map.put("dob", rs.getDate("dob"));
        map.put("aadhaar_masked", rs.getString("aadhaar_masked"));
        map.put("pan_masked", rs.getString("pan_masked"));
        map.put("country", rs.getString("country"));
        map.put("state", rs.getString("state"));
        map.put("district", rs.getString("district"));
        map.put("city", rs.getString("city"));
        map.put("pincode", rs.getString("pincode"));
        map.put("created_at", rs.getTimestamp("created_at"));
        map.put("updated_at", rs.getTimestamp("updated_at"));
        return map;
    }
}
