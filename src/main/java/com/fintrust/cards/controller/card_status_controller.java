package com.fintrust.cards.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.fintrust.db.DBConnection;

public class card_status_controller extends SelectorComposer<Window> {

	@Wire
	Listbox atmRequestList;

	static {
		createCardShema();
	}

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection = DBConnection.getConnection();
		// String atmCardNumber=(String)Sessions.getCurrent().getAttribute("atmNumber");
		// long atmNumber = (Long) Long.parseLong(atmCardNumber);

		String sql = "select * from card_request where customer_id =?";
		PreparedStatement ptsm = connection.prepareStatement(sql);
		long customer_id = 1l;
		ptsm.setLong(1, customer_id); // ****TAKE IT FROM SESSIONOS

		ResultSet rs = ptsm.executeQuery();
		while (rs.next()) {

			Listitem li = new Listitem();

			Listcell lc = new Listcell((String) rs.getString("account_no"));
			li.appendChild(lc);

			lc = new Listcell((String) rs.getString("card_type"));
			li.appendChild(lc);

			lc = new Listcell((String) rs.getString("card_category"));
			li.appendChild(lc);

			lc = new Listcell((String) rs.getString("address"));
			li.appendChild(lc);

			lc = new Listcell((String) rs.getString("remarks"));
			li.appendChild(lc);

			lc = new Listcell((String) rs.getString("card_request_status"));
			li.appendChild(lc);

			lc = new Listcell((String) rs.getString("remarks"));
			li.appendChild(lc);

			atmRequestList.appendChild(li);
		}

	}

	// Table creation

	private static boolean createCardShema() {
		String query = """
				    CREATE TABLE IF NOT EXISTS card_request (
				        request_no BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
				        card_type VARCHAR(30) NOT NULL,
				        card_category VARCHAR(30) NOT NULL,
				        address VARCHAR(255) NOT NULL,
				        remarks VARCHAR(255) DEFAULT NULL,
				        card_request_status VARCHAR(20) DEFAULT 'PENDING',
				        customer_id BIGINT UNSIGNED NOT NULL,
				        account_no BIGINT NOT NULL,
				        requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
				        PRIMARY KEY (request_no),
				        FOREIGN KEY (account_no) REFERENCES account(account_no),
				        FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
				    );
				""";

		Statement stmt;
		try {
			stmt = DBConnection.getConnection().createStatement();
			stmt.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String args[]) {
		System.out.println(createCardShema());
	}

}
