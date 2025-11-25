package com.fintrust.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.zkoss.zhtml.Messagebox;

import com.fintrust.db.DBConnection;
import com.fintrust.model.Nominee;


public class NomineeDAO {
	public static boolean createNomineeSchema() {
		String q = "CREATE TABLE nominee (\r\n"
				+ "id BIGINT UNSIGNED PRIMARY KEY,"
				+ "    nominee_id BIGINT UNSIGNED NOT NULL,\r\n"
				+ "    nominee_name VARCHAR(100) NOT NULL,\r\n"
				+ "    nominee_relation VARCHAR(50) NOT NULL\r\n"
				+ ");";
		try {
			Statement statement = DBConnection.getConnection().createStatement();
			statement.executeUpdate(q);
			System.out.println("customer table created");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
	}
	
	
	public Long createNominee(Nominee nominee) throws SQLException {
		createNomineeSchema();
		String q = "INSERT INTO nominee VALUES ( ?, ?, ?);";
		try(PreparedStatement ps = DBConnection.getConnection().prepareStatement(q, Statement.RETURN_GENERATED_KEYS)){
			
			ps.setLong(1, nominee.getNominee_id());
			ps.setString(2, nominee.getNominee_name());
			ps.setString(3, nominee.getNominee_relation());

			ps.executeUpdate();
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return -1l;
		}
	
	}
	
	public Nominee getNominee(long nominee_id) {
		Nominee nominee = new Nominee();
		String q = "select * from nominee where nominee_id = ?";
		try(PreparedStatement statement = DBConnection.getConnection().prepareStatement(q)){
			
			statement.setLong(1, nominee_id);
			ResultSet rs = statement.executeQuery();
			if(rs.next()) {
				System.out.println("Account created successfully");
				return nominee;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return null; 
	}
}
