package com.fintrust.cards.controller;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.fintrust.db.DBConnection;

public class manageCardController extends SelectorComposer<Window> {

    @Wire
    private Listbox cardListbox;  
     
    @Wire
    private Button manageBtn;

   private static boolean createCardShemea() {
	   String query = """
			    CREATE TABLE IF NOT EXISTS card (
			        card_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,   -- internal ID
			        card_number CHAR(16) NOT NULL UNIQUE,              -- real card number
			        customer_id BIGINT UNSIGNED NOT NULL,
			        account_no BIGINT  NOT NULL,
			        cvv CHAR(3) NOT NULL,
			        pin CHAR(4) NOT NULL,
			        issued_date DATE DEFAULT (CURDATE()),
			        expiry_date DATE GENERATED ALWAYS AS (issued_date + INTERVAL 3 YEAR) STORED,
			        card_status VARCHAR(20) DEFAULT 'Active',
			        maximum_limit INT DEFAULT 50000,

			        PRIMARY KEY (card_id),
			        FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
			        FOREIGN KEY (account_no) REFERENCES account(account_no)
			    ) ENGINE=InnoDB;
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
    
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        List<List<String>> cardData = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM card where customer_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            Long custId= 1l;                                     //take it from session**(((((((((((

            ps.setLong(1,custId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(rs.getLong("card_number")));
                row.add(rs.getLong("customer_id")+"");
                row.add(rs.getLong("account_no")+"");
                row.add(rs.getString("cvv"));
                row.add(rs.getString("pin"));
                row.add(String.valueOf(rs.getDate("issued_date")));
                row.add(String.valueOf(rs.getDate("expiry_date")));
                row.add(rs.getString("card_status"));
                cardData.add(row);
            }

            ListModelList<List<String>> model = new ListModelList<>(cardData);
            cardListbox.setModel(model);

        } catch (SQLException e) {
            Messagebox.show("Error loading card data: " + e.getMessage(), "Database Error", Messagebox.OK, Messagebox.ERROR);
            e.printStackTrace();
        }
    }
    
    @Listen("onClick=button")
    public void showDetails(Event e)
    {
       
          Component targetButton= e.getTarget();
       
          Listitem item =  (Listitem) targetButton.getParent().getParent();
          
          String atmNumber = ((Listcell) item.getChildren().get(0)).getLabel();  
          System.out.println(atmNumber);
          Sessions.getCurrent().setAttribute("atmNumber", atmNumber);
          Executions.sendRedirect("/Card/cardDetails.zul");
             
    }
    
    public static void main(String args[]) {
    	boolean isCreated = createCardShemea();
    	System.out.println(isCreated);
    }
  
}












