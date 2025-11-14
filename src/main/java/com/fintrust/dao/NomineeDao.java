package com.fintrust.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.zkoss.zhtml.Messagebox;

import com.fintrust.db.DBConnection;
import com.fintrust.model.Nominee;


public class NomineeDao {
	public static boolean createNomineeSchema() {
		String q = "CREATE TABLE nominee (\r\n"
				+ "    nominee_id BIGINT UNSIGNED PRIMARY KEY,\r\n"
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
	
	
	public boolean createNominee(Nominee nominee) {
		String q = "INSERT INTO nominee VALUES ( ?, ?, ?);";
		try(PreparedStatement statement = DBConnection.getConnection().prepareStatement(q)){
			
			statement.setLong(1, nominee.getNominee_id());
			statement.setString(2, nominee.getNominee_name());
			statement.setString(3, nominee.getNominee_relation());

			if(statement.executeUpdate()>0) {
				System.out.println("nominee created successfully");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show(e.getMessage());
		}
		return false;
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
