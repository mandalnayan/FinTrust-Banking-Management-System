package com.fintrust.util;

import java.util.Map;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

/**
 * Now every page that includes the above init tag will automatically show pending session notifications.
 */
public class PageInitNotifier implements Initiator {

	@Override
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		 NotificationUtil.showIfExists();
		
	}
}
