package com.fintrust.admin.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.fintrust.model.AdminKycDTO;
import com.fintrust.service.AdminKycService;
import com.fintrust.util.NotificationUtil;

public class AdminKycController extends SelectorComposer<Component> {

	@Wire
	private Listbox kycList;

	@Wire
	private Textbox remarksBox;

	private final AdminKycService service = new AdminKycService();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		loadKycs();
	}

	private void loadKycs() {
		List<AdminKycDTO> list = service.fetchPendingKycs();

		kycList.setModel(new ListModelList<>(list));
	}

	@Listen("onClick=#approveBtn")
	public void approve() {
		Listitem request = kycList.getSelectedItem();
		if (request == null) {
			NotificationUtil.showInstant("warning", "Select account to approve");
			return;
		}
		AdminKycDTO dto = request.getValue();
		service.approve(dto.getUserId());
		loadKycs();
		NotificationUtil.showInstant("info", "KYC Approved");
		
	}

	@Listen("onClick=#rejectBtn")
	public void reject() {
		Listitem request = kycList.getSelectedItem();
		if (request == null) {
			NotificationUtil.showInstant("warning", "Select account to reject");
			return;
		}
		AdminKycDTO dto = request.getValue();
		if (remarksBox.getValue().isBlank()) {			
			NotificationUtil.showInstant("warning", "Remarks required");
			return;
		}

		service.reject(dto.getUserId(), remarksBox.getValue());
		loadKycs();		
		NotificationUtil.showInstant("warning", "KYC Rejected");
	}
}
