package com.fintrust.cards.controller;

import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.*;

import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.model.CardRequest;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.impl.AccountUpdateRequestDao;
import com.fintrust.dao.impl.CardsDAOImpl;
import com.fintrust.dao.impl.AccountUpdateRequestDao;

import java.util.*;

public class CardApprovalController extends SelectorComposer<Component> {

	@Wire
	private Listbox requestList;
	@Wire
	Button approveBtn, rejectBtn;
	private Long currentUserId;

	private AccountUpdateRequestDao dao = new AccountUpdateRequestDao();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		currentUserId = (Long) Sessions.getCurrent().getAttribute("user_id");
		if (currentUserId == null)
			return;
		loadPendingCardRequests();
	}

	private void loadPendingCardRequests() throws Exception {
		CardsDAOImpl ob = new CardsDAOImpl();
		List<CardRequest> list = ob.getPendingCardRequests();
		if (list.size() == 0) {
			approveBtn.setVisible(false);
			rejectBtn.setVisible(false);

			Hbox box = new Hbox();
			box.setWidth("100%");
			box.setHeight("50px");
			box.setPack("center");
			box.setAlign("center");

			Label label = new Label("No Card Request Pending for the Approvel");
			label.setStyle("font-size:22px");
			box.appendChild(label);

			requestList.getParent().appendChild(box);
			return;
		}
		requestList.setModel(new ListModelList<>(list));
	}

	@Listen("onClick = #approveBtn")
	public void approveCardRequest() throws Exception {
		if (requestList.getSelectedItem() == null) {
			NotificationUtil.showInstant("warning", "Please select one Card Request first!");
			return;
		}

		CardRequest req = requestList.getSelectedItem().getValue();

		CardsDAOImpl ob = new CardsDAOImpl();
		System.out.println(req);
		ob.approveCardRequests(req);
		loadPendingCardRequests();
		NotificationUtil.showInstant("info", "Request Card approved successfully!");

	}

	@Listen("onClick = #rejectBtn")
	public void rejectRequest() throws Exception {
		if (requestList.getSelectedItem() == null) {
			Messagebox.show("Please select one account first!");
			return;
		}
		CardRequest req = requestList.getSelectedItem().getValue();
		CardsDAOImpl ob = new CardsDAOImpl();
		ob.rejectCardRequests(req);

		NotificationUtil.showInstant("info", "Request rejected!");
		loadPendingCardRequests();
	}
}
