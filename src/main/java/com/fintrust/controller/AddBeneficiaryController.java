package com.fintrust.controller;

import java.sql.Connection;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import com.fintrust.model.Beneficiary;
import com.fintrust.util.NotificationUtil;

import com.fintrust.dao.impl.BeneficiaryDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.dao.BeneficiaryDAO;

public class AddBeneficiaryController extends SelectorComposer<Component> {

    @Wire private Textbox nameBox, bankBox, ifscBox;
    @Wire private Longbox accountBox;
    @Wire private Label statusLabel;

    @Listen("onClick=#addBtn")
    public void addBeneficiary() {
        String name = nameBox.getValue();
        Long accountNumber = accountBox.getValue();
        String bank = bankBox.getValue();
        String ifsc = ifscBox.getValue();

        if (name.isEmpty() || accountNumber == null || toString().valueOf(accountNumber).length() != 12 || bank.isEmpty() || ifsc.isEmpty() || ifsc.length() != 11) {
            NotificationUtil.showInstant("warning", " Fill all fields!");
            return;
        }

        Beneficiary b = new Beneficiary();
        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
        b.setUserId(userId); // Replace with logged-in user's ID later
        b.setName(name);
        b.setAccountNumber(accountNumber);
        b.setBankName(bank);
        b.setIfscCode(ifsc);

        try {
        	Connection conn = DBConnection.getConnection();
        	BeneficiaryDAO beneficiaryDAO = new BeneficiaryDAOImpl(conn);
            Long beneficiaryId = beneficiaryDAO.create(b);
            if (beneficiaryId != -1) {
                statusLabel.setValue("Beneficiary added successfully! \nYour beneficiaryId = " + beneficiaryId);
                NotificationUtil.push("info", "Beneficiary added successfully! \nYour beneficiaryId = " + beneficiaryId);
                Executions.sendRedirect("/user/userDashboard.zul");
            } else {
            	NotificationUtil.showInstant("error", "Failed to add beneficiary.");
            }
        } catch (Exception e) {
            e.printStackTrace();

            NotificationUtil.showInstant("error", "Faild to add. " + e.getMessage());
        }
    }
}

