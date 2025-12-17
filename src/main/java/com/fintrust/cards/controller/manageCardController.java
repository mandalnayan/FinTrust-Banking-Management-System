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
       
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        List<List<String>> cardData = new ArrayList<>();

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM cards where user_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            Long user_id = (Long) Sessions.getCurrent().getAttribute("user_id");                                    //take it from session**(((((((((((
          
            ps.setLong(1, user_id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                List<String> row = new ArrayList<>();  
                
                row.add(rs.getString("card_number_masked"));

                row.add(rs.getString("card_type")+"");
                row.add(rs.getLong("account_number")+"");
               // row.add(rs.getString("cvv"));
               // row.add(rs.getString("pin"));
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
          
          String card_number_masked = ((Listcell) item.getChildren().get(0)).getLabel();
          
          System.out.println(card_number_masked);
          
          Sessions.getCurrent().setAttribute("card_number_masked", card_number_masked);
          
          Executions.sendRedirect("/user/card/cardDetails.zul");
             
    } 
 
      }
