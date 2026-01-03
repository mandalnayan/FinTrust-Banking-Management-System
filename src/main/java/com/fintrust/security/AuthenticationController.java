package com.fintrust.security;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zul.Messagebox;


/**
 * This controller will load before performing any operation on the zul page.
 */
public class AuthenticationController implements Initiator{

	@Override
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		String user = (String) Sessions.getCurrent().getAttribute("user_email");
		if (user == null || user.isBlank()) {
		//	Executions.sendRedirect("/home.zul");
		}
	}

}
