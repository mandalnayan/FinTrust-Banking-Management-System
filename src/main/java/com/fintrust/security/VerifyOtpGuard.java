package com.fintrust.security;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericInitiator;

/**
 * GenericInitiator is a ZK lifecycle component used to run logic BEFORE a ZUL page is created.
 * It runs inside ZK’s page initialization phase, meaning it executes before:

✔ Any component is created
✔ Any ViewModel is instantiated
✔ Any event listeners run
✔ DOM tree is created
 */
public class VerifyOtpGuard extends GenericInitiator{
	
	@Override
		public void doInit(org.zkoss.zk.ui.Page page, Map<String, Object> args) throws Exception {
			super.doInit(page, args);
			   Boolean allowed = (Boolean) Sessions.getCurrent().getAttribute("otp_allowed");

		        if (allowed == null || !allowed) {
		            Executions.sendRedirect("/user/login.zul");
		        }
		}
}
 
