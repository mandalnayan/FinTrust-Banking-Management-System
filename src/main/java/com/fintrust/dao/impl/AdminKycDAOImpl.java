package com.fintrust.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.fintrust.db.DBConnection;
import com.fintrust.model.AdminKycDTO;

public class AdminKycDAOImpl {

    public List<AdminKycDTO> getPendingKycs() {

        List<AdminKycDTO> list = new ArrayList<>();

        String sql = """
            SELECT ud.details_id, u.user_id, u.full_name, u.phone, u.email,
                   ud.dob, ud.gender, ud.aadhaar_masked, ud.pan_masked,
                   ud.country, ud.state, ud.district, ud.city, ud.pincode,
                   ud.photo_name, ud.address_proof_name,
                   u.kyc_status,ud.updated_at
            FROM user_details ud
            JOIN users u ON u.user_id = ud.user_id
            WHERE u.kyc_status = 'REQUESTED'
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                AdminKycDTO dto = new AdminKycDTO();

                dto.setKycId(rs.getInt("details_id"));
                dto.setUserId(rs.getInt("user_id"));
                dto.setFullName(rs.getString("full_name"));
                dto.setMaskedPhone(rs.getString("phone"));
                dto.setEmail(rs.getString("email"));

                dto.setDob(rs.getDate("dob").toLocalDate());
                dto.setGender(rs.getString("gender"));
                dto.setMaskedAadhaar(rs.getString("aadhaar_masked"));
                dto.setMaskedPan(rs.getString("pan_masked"));

                dto.setCountry(rs.getString("country"));
                dto.setState(rs.getString("state"));
                dto.setDistrict(rs.getString("district"));
                dto.setCity(rs.getString("city"));
                dto.setPincode(rs.getString("pincode"));

                dto.setPhotoFileName(rs.getString("photo_name"));
                dto.setAddressProofFileName(rs.getString("address_proof_name"));
                dto.setKycStatus(rs.getString("kyc_status"));
                Timestamp updateDate = rs.getTimestamp("updated_at");
                if (updateDate != null)
                		dto.setSubmittedAt(updateDate.toLocalDateTime());

                list.add(dto);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public void updateStatus(int userId, String status, String remarks, Long adminId) {

        String sql = """
			           UPDATE users
        		     		    SET kyc_status = ?, kyc_reviewed_by = ?,
        		     		    updated_at = NOW()
        								WHERE user_id = ?;
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setLong(2, adminId);
            ps.setLong(3, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}