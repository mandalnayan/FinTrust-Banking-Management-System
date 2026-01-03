package com.fintrust.service;

import com.fintrust.model.AccountCloseRequest;

import org.zkoss.zul.Messagebox;

import com.fintrust.dao.impl.AccountCloseRequestDao;

public class AccountCloseRequestService {
	AccountCloseRequestDao closeReqDao;
	public AccountCloseRequestService() {
		closeReqDao = new AccountCloseRequestDao();
	}
	
	public boolean saveReq(AccountCloseRequest req) {
		Boolean isExist = closeReqDao.isRequestExist(req.getAccountNo());
//		Some DB error
		if(isExist == null) {
			System.out.println("DB error");
		} else if(!isExist) {
			return closeReqDao.saveRequest(req);
		}
		return false;
		
	}
}
