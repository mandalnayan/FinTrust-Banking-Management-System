package com.fintrust.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;

public class LoginedHomeController extends SelectorComposer<Window>{

	/*
	 * @Override public ComponentInfo doBeforeCompose(Page page, Component parent,
	 * ComponentInfo compInfo) { if (Sessions.getCurrent().getAttribute("user") ==
	 * null) { Executions.sendRedirect("userLogin.zul"); return null; } return
	 * super.doBeforeCompose(page, parent, compInfo);
	 * 
	 * }
	 */	
}
