package com.fintrust.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import com.fintrust.model.BeneficiaryModel;
import com.fintrust.dao.BeneficiaryDao;

public class AddBeneficiaryController extends SelectorComposer<Component> {

    @Wire private Textbox nameBox, accountBox, bankBox, ifscBox;
    @Wire private Label statusLabel;

    @Listen("onClick=#addBtn")
    public void addBeneficiary() {
        String name = nameBox.getValue();
        String account = accountBox.getValue();
        String bank = bankBox.getValue();
        String ifsc = ifscBox.getValue();

        if (name.isEmpty() || account.isEmpty() || bank.isEmpty() || ifsc.isEmpty()) {
            Clients.showNotification(" Fill all fields!");
            return;
        }

        BeneficiaryModel b = new BeneficiaryModel();
        b.setUserId(1); // Replace with logged-in user's ID later
        b.setName(name);
        b.setAccountNumber(account);
        b.setBankName(bank);
        b.setIfscCode(ifsc);

        try {
            boolean result = BeneficiaryDao.addBeneficiary(b);
            if (result) {
                statusLabel.setValue(" Beneficiary added successfully!");
                Clients.showNotification("Beneficiary added!");
            } else {
                statusLabel.setValue("Failed to add beneficiary.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Clients.showNotification("Error: " + e.getMessage());
        }
    }
}

