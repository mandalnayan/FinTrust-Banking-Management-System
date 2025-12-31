package com.fintrust.util;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.Clients;

import java.util.Map;

public class LoginInit implements Initiator {

    @Override
    public void doInit(Page page, Map<String, Object> args) {
        String error = Executions.getCurrent().getParameter("error");

        if ("true".equals(error)) {
        	NotificationUtil.showInstant("error", "Invalid username or password");
        	
            // Optional: remove ?error=true from URL
            Clients.evalJavaScript(
                "history.replaceState({}, document.title, window.location.pathname);"
            );
        }
    }
}

