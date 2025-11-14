package com.fintrust.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fintrust.db.DBConnection;
import com.fintrust.model.ContactModel;

public class ContactDAO {

	static {
		createTables();
	}
	
    private static final String CREATE_TICKET_TABLE =
            "CREATE TABLE IF NOT EXISTS support_tickets (" +
            " id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
            " customer_name VARCHAR(100), " +
            " mobile VARCHAR(15), " +
            " email VARCHAR(100), " +
            " customer_id VARCHAR(50), " +
            " category VARCHAR(100), " +
            " subcategory VARCHAR(100), " +
            " priority VARCHAR(20), " +
            " subject VARCHAR(200), " +
            " description TEXT, " +
            " preferred_contact_method VARCHAR(50), " +   
            " preferred_time VARCHAR(50), " +
            " status VARCHAR(20) DEFAULT 'Open', " +
            " created_at DATETIME, " +
            " updated_at DATETIME " +
            ")";

    private static final String CREATE_ATTACHMENT_TABLE =
            "CREATE TABLE IF NOT EXISTS ticket_attachments (" +
            " id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
            " ticket_id BIGINT, " +
            " file_name VARCHAR(255), " +
            " uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            " FOREIGN KEY (ticket_id) REFERENCES support_tickets(id) ON DELETE CASCADE " +
            ")";

    private static final String INSERT_TICKET =
            "INSERT INTO support_tickets (" +
            " customer_name, mobile, email, customer_id, category, subcategory, " +
            " priority, subject, description, preferred_contact_method, preferred_time, " +
            " status, created_at, updated_at" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_ATTACHMENT =
            "INSERT INTO ticket_attachments (ticket_id, file_name) VALUES (?, ?)";

    public ContactDAO() {
        createTables();
    }

    private static void createTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(CREATE_TICKET_TABLE);
            stmt.executeUpdate(CREATE_ATTACHMENT_TABLE);

            System.out.println("Tables created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    public long insert(ContactModel model) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_TICKET, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, model.getCustomerName());
            pstmt.setString(2, model.getMobile());
            pstmt.setString(3, model.getEmail());
            pstmt.setString(4, model.getCustomerId());
            pstmt.setString(5, model.getCategory());
            pstmt.setString(6, model.getSubcategory());
            pstmt.setString(7, model.getPriority());
            pstmt.setString(8, model.getSubject());
            pstmt.setString(9, model.getDescription());
            pstmt.setString(10, model.getPreferredContactMethod()); // FIXED
            pstmt.setString(11, model.getPreferredTime());
            pstmt.setString(12, "Open");
            pstmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            long ticketId = -1;

            if (rs.next()) {
                ticketId = rs.getLong(1);
            }

            // Insert attachments (if present)
            if (ticketId > 0 && model.getAttachments() != null && !model.getAttachments().isEmpty()) {
                insertAttachments(conn, ticketId, model.getAttachments());
            }

            return ticketId;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private void insertAttachments(Connection conn, long ticketId, List<String> attachments) throws SQLException {

        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_ATTACHMENT)) {

            for (String fileName : attachments) {
                pstmt.setLong(1, ticketId);
                pstmt.setString(2, fileName);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
    }

    
    public ContactModel findById(long id) {

        String sql = "SELECT * FROM support_tickets WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ContactModel model = new ContactModel();

                model.setId(rs.getLong("id"));
                model.setCustomerName(rs.getString("customer_name"));
                model.setMobile(rs.getString("mobile"));
                model.setEmail(rs.getString("email"));
                model.setCustomerId(rs.getString("customer_id"));
                model.setCategory(rs.getString("category"));
                model.setSubcategory(rs.getString("subcategory"));
                model.setPriority(rs.getString("priority"));
                model.setSubject(rs.getString("subject"));
                model.setDescription(rs.getString("description"));
                model.setPreferredContactMethod(rs.getString("preferred_contact_method")); // FIXED
                model.setPreferredTime(rs.getString("preferred_time"));
                model.setStatus(rs.getString("status"));
                model.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                model.setAttachments(getAttachments(id));

                return model;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<String> getAttachments(long ticketId) {

        String sql = "SELECT file_name FROM ticket_attachments WHERE ticket_id = ?";
        List<String> files = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ticketId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                files.add(rs.getString("file_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

}
