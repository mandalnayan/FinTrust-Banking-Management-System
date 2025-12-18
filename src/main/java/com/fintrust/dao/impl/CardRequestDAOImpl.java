package com.fintrust.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.db.DBConnection;
import com.fintrust.model.CardRequest;

public class CardRequestDAOImpl {

	public static List<CardRequest> loadRequests() {

		List<CardRequest> list = new ArrayList<>();

		String sql = "SELECT * FROM card_request WHERE user_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
			ps.setLong(1, userId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					CardRequest dto = new CardRequest();
					dto.setRequestNo(rs.getLong("request_no"));
					dto.setAccountNo(rs.getLong("account_no"));
					dto.setCardType(rs.getString("card_type"));
					dto.setCardCategory(rs.getString("card_category"));
					dto.setAddress(rs.getString("address"));
					dto.setRemarks(rs.getString("remarks"));
					dto.setStatus(rs.getString("card_request_status"));
					list.add(dto);
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
