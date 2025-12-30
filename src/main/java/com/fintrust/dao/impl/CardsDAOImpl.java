package com.fintrust.dao.impl;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.zkoss.zul.Messagebox;

import com.fintrust.dao.CardsDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.CardRequest;
import java.util.concurrent.ThreadLocalRandom;

/**
 * JDBC implementation of CardsDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements and
 * follow banking-grade standards.
 */
public class CardsDAOImpl implements CardsDAO {

	private final Connection connection;

	/**
	 * Constructor for dependency injection.
	 *
	 * @param connection JDBC connection managed externally
	 */
	public CardsDAOImpl() {
		connection = DBConnection.getConnection();
	}

	@Override
	public long create(long userId, Long accountId, String cardNumberMasked, String cardBin, String cardType,
			String provider, Date expiryDate, String status) throws SQLException {

		String sql = """
				    INSERT INTO cards
				    (user_id, account_id, card_number_masked, card_bin, card_type, provider, expiry_date, status)
				    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setLong(1, userId);
			if (accountId != null)
				ps.setLong(2, accountId);
			else
				ps.setNull(2, Types.BIGINT);
			ps.setString(3, cardNumberMasked);
			ps.setString(4, cardBin);
			ps.setString(5, cardType.toLowerCase());
			ps.setString(6, provider != null ? provider.toLowerCase() : null);
			ps.setDate(7, expiryDate);
			ps.setString(8, status != null ? status.toLowerCase() : "active");

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
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
				if (rs.next())
					return mapRow(rs);
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
				while (rs.next())
					list.add(mapRow(rs));
			}
		}

		return list;
	}

	@Override
	public List<Map<String, Object>> findAll() throws SQLException {
		String sql = "SELECT * FROM cards ORDER BY issued_date DESC";
		List<Map<String, Object>> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			while (rs.next())
				list.add(mapRow(rs));
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

	public List<CardRequest> getPendingCardRequests() {
		List<CardRequest> cardRequestList = new ArrayList<>();
		String sql = "SELECT * FROM card_request WHERE card_request_status = 'PENDING'";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				CardRequest currCardReq = new CardRequest();
				currCardReq.setRequest_no(rs.getLong("request_no"));
				currCardReq.setCard_type(rs.getString("card_type"));
				currCardReq.setCard_category(rs.getString("card_category"));
				currCardReq.setAddress(rs.getString("address"));
				currCardReq.setRemarks(rs.getString("remarks"));
				currCardReq.setCard_request_status(rs.getString("card_request_status"));
				currCardReq.setUser_id(rs.getLong("user_id"));
				currCardReq.setAccount_no(rs.getLong("account_no"));
				currCardReq.setRequested_at(rs.getObject("requested_at", LocalDateTime.class));
				currCardReq.setApproved_at(rs.getObject("approved_at", LocalDateTime.class));
				cardRequestList.add(currCardReq);
			}

		} catch (SQLException e) {
			Messagebox.show("Error while fetching pending Cards update requests:\n" + e.getMessage());
			e.printStackTrace();
		}

		return cardRequestList;
	}

	public void approveCardRequests(CardRequest cr) {
      System.out.println(cr);
    
        String uuid = UUID.randomUUID().toString();
        String digitsOnly = uuid.replaceAll("\\D", "");
        String cardNumber = digitsOnly.substring(0, 16);
        String pin = ThreadLocalRandom.current().nextInt(100, 1000) + "";

        String sql = "INSERT INTO cards " +
                     "(user_id, card_number_masked, last4, pin_hash, expiry_date, provider, card_type, account_number) " +
                     "VALUES (?,?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setLong(1, cr.getUser_id());
            pst.setString(2, cardNumber);                
            pst.setString(3, cardNumber.substring(12));
            pst.setString(4, pin);
            pst.setDate(5, java.sql.Date.valueOf(LocalDate.now().plusYears(3)));
            if(cr.getCard_category().equalsIgnoreCase("visa"))
            	 pst.setString(6, "visa"); 
            else {
            	
            	if(cr.getCard_category().equalsIgnoreCase("mastercard"))
            		 pst.setString(6, "mastercard");
            	else
            		 pst.setString(6, "rupay"); 
            	
            }
            
          
            if(cr.getCard_category().equalsIgnoreCase("Credit Card"))
            	 pst.setString(7, "Credit");
            else
            {
            	if(cr.getCard_category().equalsIgnoreCase("Debit Card")) {
            	   pst.setString(7, "Debit");
            	}
            	else {
            		pst.setString(7, "Prepaid");
            	}
            	}  
            
            pst.setLong(8, cr.getAccount_no());

            int n = pst.executeUpdate();
          
            
                String sqlUpdate = "UPDATE card_request SET card_request_status=?, approved_at=NOW() WHERE request_no=?";
                try (PreparedStatement pst1 = con.prepareStatement(sqlUpdate)) {
                    pst1.setString(1, "APPROVED");
                    pst1.setLong(2, cr.getRequest_no());
                    pst1.executeUpdate();
             }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void rejectCardRequests(CardRequest cr) {
		
		   String sqlUpdate = "UPDATE card_request SET card_request_status=?, approved_at=NOW() WHERE request_no=?";
           try ( Connection con=DBConnection.getConnection()  ;PreparedStatement pst1 = con.prepareStatement(sqlUpdate)) {
               pst1.setString(1, "REJECTED");
               pst1.setLong(2, cr.getRequest_no());
               pst1.executeUpdate();
        
           } catch (Exception e) {
                 e.printStackTrace();
        }
		
	   }
	
	public static void main(String[] args) {
		CardsDAOImpl ob = new CardsDAOImpl();
		System.err.println(ob.getPendingCardRequests());

		String uuid = UUID.randomUUID().toString();
		String digitsOnly = uuid.replaceAll("\\D", "");
		String cardNumber = digitsOnly.substring(0, 16);
		System.out.println(cardNumber);
	}

}
