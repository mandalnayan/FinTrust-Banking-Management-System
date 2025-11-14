package com.fintrust.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

public class SessionTimeOutController extends SelectorComposer<Window>{
	
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
        Clients.showNotification("Session Logout", "warning", null, "top_center", 3000);

		Executions.sendRedirect("/user/login.zul");
	}
}
