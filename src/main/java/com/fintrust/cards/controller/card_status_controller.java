package com.fintrust.cards.controller;

import java.util.List;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import org.zkoss.zul.Window;

import com.fintrust.dao.impl.CardRequestDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.CardRequest;

public class card_status_controller extends SelectorComposer<Window> {

	@Wire
	Listbox atmRequestList;

	@Override
	public void doAfterCompose(Window comp) throws Exception {		
		super.doAfterCompose(comp);
		 List<CardRequest> cardList = CardRequestDAOImpl.loadRequests();
		 if (cardList == null) alert("Hii");
		 else
		 atmRequestList.setModel(new ListModelList<>(cardList));
		}

	}

