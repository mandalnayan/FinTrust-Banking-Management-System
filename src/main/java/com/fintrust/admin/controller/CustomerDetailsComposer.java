package com.fintrust.admin.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.fintrust.db.DBConnection;

public class CustomerDetailsComposer extends SelectorComposer<Window> {

    @Wire
    private Listbox customerList;

    @Wire
    private Combobox cmbSearchType, cmbStatus;

    @Wire
    private Textbox txtSearchValue;

    @Wire
    private Label lblTotal, lblActive, lblBlocked, lbl2FA;

    private List<Map<String, Object>> allCustomers = new ArrayList<>();

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        cmbStatus.setSelectedIndex(0);
        
        loadAllCustomers();         // Load from DB
		applyModel(allCustomers);   // Set model to listbox
		updateSummary(allCustomers); 
    }

    
    private void loadAllCustomers() {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM users where role = 'ROLE_USER' ORDER BY user_id DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            allCustomers.clear();

            while (rs.next()) {
                Map<String, Object> c = new HashMap<>();
                c.put("customer_id", rs.getLong("user_id"));
                c.put("name", rs.getString("full_name"));
                c.put("email", rs.getString("email"));
                c.put("phone", rs.getString("phone"));
                c.put("status", rs.getString("status"));
                allCustomers.add(c);
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Error loading customers: " + e.getMessage());
        }
    }

   
    private void applyModel(List<Map<String, Object>> list) {
        customerList.setModel(new ListModelList<>(list));
    }

    
    public void searchBySpecificField() {

        String field = cmbSearchType.getValue();
        String searchValue = txtSearchValue.getValue().trim().toLowerCase();

        if (field == null || field.isEmpty()) {
            Messagebox.show("Please choose a search type!");
            return;
        }

        List<Map<String, Object>> filtered = new ArrayList<>();

        for (Map<String, Object> c : allCustomers) {
            String columnValue = String.valueOf(c.get(field)).toLowerCase();

            if (columnValue.contains(searchValue)) {
                filtered.add(c);
            }
        }

        // Also apply gender & status filters
        filtered = applyAdvancedFilters(filtered);
        applyModel(filtered);
        updateSummary(filtered);
    }

    @Listen("onChange=#cmbStatus")
    public void filterList() {
        List<Map<String, Object>> filtered = applyAdvancedFilters(allCustomers);
        applyModel(filtered);
        updateSummary(filtered);
    }

    private List<Map<String, Object>> applyAdvancedFilters(List<Map<String, Object>> list) {
        String status = cmbStatus.getValue();

        List<Map<String, Object>> filtered = new ArrayList<>();

        for (Map<String, Object> c : list) {
            if (!status.isEmpty() && !status.equalsIgnoreCase((String) c.get("status")))
                continue;
 
            filtered.add(c);
        }

        return filtered;
    }

    
    private void updateSummary(List<Map<String, Object>> list) {

        int active = 0, blocked = 0;

        for (Map<String, Object> c : list) {
            String s = (String) c.get("status");

            if ("Active".equalsIgnoreCase(s))
                active++;

            if ("Blocked".equalsIgnoreCase(s))
                blocked++;
        }

        lblTotal.setValue("Total Customers: " + list.size());
        lblActive.setValue("Active: " + active);
        lblBlocked.setValue("Blocked: " + blocked);
    }
    
    
    @Listen("onClick=#Search")
    public void searchByFilter(Event e) {

        Comboitem selected = cmbSearchType.getSelectedItem();
        if (selected == null) {
            Messagebox.show("Please choose a search type!");
            return;
        }

        String field = selected.getValue();   // <--- FIXED
        String searchValue = txtSearchValue.getValue().trim().toLowerCase();

        List<Map<String, Object>> filtered = new ArrayList<>();

        for (Map<String, Object> c : allCustomers) {

            String columnValue = String.valueOf(c.get(field)).toLowerCase();

            if (columnValue.contains(searchValue)) {
                filtered.add(c);
            }
        }

        customerList.setModel(new ListModelList<>(filtered));
    }

    
    
}
