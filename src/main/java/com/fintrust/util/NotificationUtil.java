
	package com.fintrust.util;

	import org.zkoss.zk.ui.Sessions;
	import org.zkoss.zk.ui.util.Clients;

import com.fintrust.model.Notification;

	public class NotificationUtil {

	    private static final String SESSION_MESSAGE = "GLOBAL_NOTIFICATION_MESSAGE";
	    private static final String SESSION_TYPE = "GLOBAL_NOTIFICATION_TYPE";

	    private NotificationUtil() {}

	    // Save message for next page (during redirect)
	    public static void push(String type, String message) {
	        Sessions.getCurrent().setAttribute(SESSION_MESSAGE, message);
	        Sessions.getCurrent().setAttribute(SESSION_TYPE, type);
	    }
	    
	    // Save message for next page (during redirect)
	    public static void push(Notification notification) {
	    	String type = notification.getType(), message = notification.getMessage();
	        Sessions.getCurrent().setAttribute(SESSION_MESSAGE, message);
	        Sessions.getCurrent().setAttribute(SESSION_TYPE, type);
	    }

	    // Show instantly (no redirect)
	    public static void showInstant(String type, String message) {
	        Clients.showNotification(message, type, null, "top_center", 5000);
	    }
	    
	 // Show instantly (no redirect)
	    public static void showInstant(Notification notification) {
	    	String type = notification.getType(), message = notification.getMessage();
	        Clients.showNotification(message, type, null, "top_center", 4000);
	    }
	    
	 // Show instantly (no redirect)
	    public static void showInstant(String type, String message, int time) {
	    	
	    	 Clients.showNotification(message, type, null, "top_center", time);
	    }

	    // Called on page load to display pending session message
	    public static void showIfExists() {
	        var session = Sessions.getCurrent();
	        String msg = (String) session.getAttribute(SESSION_MESSAGE);
	        String type = (String) session.getAttribute(SESSION_TYPE);

	        if (msg != null) {
	            Clients.showNotification(msg, type, null, "top_center", 4000);

	            // clear after showing
	            session.removeAttribute(SESSION_MESSAGE);
	            session.removeAttribute(SESSION_TYPE);
	        }
	    }
	}

