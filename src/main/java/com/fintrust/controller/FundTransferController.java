package com.fintrust.controller;

import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import com.fintrust.model.BeneficiaryModel;
import com.fintrust.dao.BeneficiaryDao;
import com.fintrust.dao.FundTransferDao;


public class FundTransferController extends SelectorComposer<Component> {

    @Wire private Textbox fromAccount;
    @Wire private Textbox toAccount;
    @Wire private Textbox ifsccode;
    @Wire private Doublebox amount;
    @Wire private Combobox beneficiaryCombo;
    @Wire private Label statusLabel;

    private List<BeneficiaryModel> beneficiaries;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

    
        int userId = 1; 
        beneficiaries = BeneficiaryDao.getBeneficiariesByUserId(userId);

        for (BeneficiaryModel b : beneficiaries) {
            Comboitem item = new Comboitem(b.getName() + " (" + b.getBankName() + ")");
            item.setValue(b);
            beneficiaryCombo.appendChild(item);
        }
    }

    @Listen("onSelect=#beneficiaryCombo")
    public void onBeneficiarySelect() {
        Comboitem selected = beneficiaryCombo.getSelectedItem();
        if (selected != null) {
            BeneficiaryModel b = selected.getValue();
            toAccount.setValue(b.getAccountNumber());
            ifsccode.setValue(b.getIfscCode());
            Clients.showNotification("Beneficiary selected: " + b.getName());
        }
    }

    @Listen("onClick=#transferBtn")
    public void transferFunds() {
        String fromAcc = fromAccount.getValue();
        String toAcc = toAccount.getValue();
        Double amt = amount.getValue();

        if (fromAcc.isEmpty() || toAcc.isEmpty() || amt == null || amt <= 0) {
            Clients.showNotification("Please enter valid transfer details!");
            return;
        }

        boolean result = FundTransferDao.transferFunds(fromAcc, toAcc, amt);
        Clients.showNotification("from=" + fromAcc + ", to=" + toAcc + ", amount=" + amt);
        if (result) {
            statusLabel.setValue("Transfer successful!");
        } else {
            statusLabel.setValue("Transfer failed! Check balance or account.");
        }
    }
}
