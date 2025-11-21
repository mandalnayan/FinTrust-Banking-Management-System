package com.fintrust.service;

import com.fintrust.model_copy.AccountCloseRequest;

import zcom.finrust.dao_copy.AccountCloseRequestDao;

public class AccountCloseRequestService {
	AccountCloseRequestDao closeReqDao;
	public AccountCloseRequestService() {
		closeReqDao = new AccountCloseRequestDao();
	}
	
	public boolean saveReq(AccountCloseRequest req) {
		return closeReqDao.saveRequest(req);
	}
}
