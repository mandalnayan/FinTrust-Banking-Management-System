package com.fintrust.admin.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.fintrust.db.DBConnection;
import com.fintrust.model.User;
import com.fintrust.model.User.Status;
import com.fintrust.service.UserServiceImpl;

public class CustomerDetailsComposer extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;

    @Wire
    private Listbox customerList;

    @Wire
    private Combobox cmbSearchType, cmbStatus;

    @Wire
    private Textbox txtSearchValue;

    @Wire
    private Label lblTotal, lblActive, lblBlocked;

    private final UserServiceImpl userService = new UserServiceImpl();

    private List<User> allCustomers = new ArrayList<>();
    private ListModelList<User> model;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        cmbStatus.setSelectedIndex(0);

        allCustomers = loadAllCustomers();
        model = new ListModelList<>(allCustomers);
        customerList.setModel(model);

        // IMPORTANT: use anonymous renderer (NOT method reference)
        customerList.setItemRenderer(new ListitemRenderer<User>() {
            @Override
            public void render(Listitem item, User user, int index) {
                renderRow(item, user);
            }
        });

        updateSummary(allCustomers);
    }

    /* ================= ROW RENDERER ================= */

    private void renderRow(Listitem item, User user) {

        item.setValue(user);

        item.appendChild(new Listcell(String.valueOf(user.getId())));
        item.appendChild(new Listcell(user.getFullName()));
        item.appendChild(new Listcell(user.getEmail()));
        item.appendChild(new Listcell(user.getPhone()));

        Listcell statusCell = new Listcell(user.getStatus().name());
        item.appendChild(statusCell);

        // ACTIONS
        Hbox actions = new Hbox();
        actions.setSpacing("8px");

        // VIEW
        Button viewBtn = new Button("View");
        viewBtn.setStyle("padding:3px 10px;background:#007bff;color:white;border:none;border-radius:4px; width:80px;");
        viewBtn.addEventListener("onClick", e -> {
            Sessions.getCurrent().setAttribute("selected_user_id", user.getId());
            Include center = (Include) item.getPage().getFellow("main_content_sec");
            center.setSrc("/admin/userDetailsPopup.zul");
        });

        // BLOCK / UNBLOCK
        Button statusBtn = new Button(user.getStatus() == Status.ACTIVE ? "Block" : "Unblock");
        statusBtn.setStyle("padding:3px 10px;background:#dc3545;color:white;border:none;border-radius:4px; width:80px;");
        statusBtn.addEventListener("onClick", e -> toggleStatus(user, statusCell, statusBtn));

        
        actions.appendChild(viewBtn);
        actions.appendChild(statusBtn);

        Listcell actionCell = new Listcell();
        actionCell.appendChild(actions);
        item.appendChild(actionCell);
    }

    /* ================= STATUS TOGGLE ================= */

    private void toggleStatus(User user, Listcell statusCell, Button btn) {

        Status newStatus = (user.getStatus() == Status.ACTIVE)
                ? Status.BLOCKED
                : Status.ACTIVE;

        if (userService.changeUserStatus(user.getId(), newStatus)) {
            user.setStatus(newStatus);

            statusCell.setLabel(newStatus.name());
            btn.setLabel(newStatus == Status.ACTIVE ? "Block" : "Unblock");

            updateSummary(model);
        }
    }

    /* ================= LOAD USERS ================= */

    private List<User> loadAllCustomers() {

        List<User> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM users WHERE role='ROLE_USER' ORDER BY user_id DESC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setStatus(Status.valueOf(rs.getString("status").toUpperCase()));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /* ================= SEARCH ================= */

    @Listen("onClick=#Search")
    public void search() {

        String field = cmbSearchType.getValue();
        String value = txtSearchValue.getValue().trim();

        if (field == null || field.isEmpty()) {
            Messagebox.show("Please choose a search type!");
            return;
        }

        List<User> filtered = new ArrayList<>();

        for (User u : allCustomers) {
            if ("Name".equalsIgnoreCase(field) && u.getFullName().contains(value)) filtered.add(u);
            if ("Email".equalsIgnoreCase(field) && u.getEmail().contains(value)) filtered.add(u);
            if ("Phone".equalsIgnoreCase(field) && u.getPhone().contains(value)) filtered.add(u);
        }

        model.clear();
        model.addAll(filtered);
        updateSummary(filtered);
    }

    /* ================= STATUS FILTER ================= */

    @Listen("onChange=#cmbStatus")
    public void filterByStatus() {

        String status = cmbStatus.getValue();
        List<User> filtered = new ArrayList<>();

        for (User u : allCustomers) {
            if (status == null || status.isEmpty()
                    || status.equalsIgnoreCase(u.getStatus().name())) {
                filtered.add(u);
            }
        }

        model.clear();
        model.addAll(filtered);
        updateSummary(filtered);
    }

    /* ================= SUMMARY ================= */

    private void updateSummary(Collection<User> list) {

        int active = 0, blocked = 0;

        for (User u : list) {
            if (u.getStatus() == Status.ACTIVE) active++;
            if (u.getStatus() == Status.BLOCKED) blocked++;
        }

        lblTotal.setValue("Total Customers: " + list.size());
        lblActive.setValue("Active: " + active);
        lblBlocked.setValue("Blocked: " + blocked);
    }
}
