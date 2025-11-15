package com.fintrust.service;

import com.fintrust.dao.AccountCloseRequestDao;
import com.fintrust.model.AccountCloseRequest;

public class AccountCloseRequestService {
	AccountCloseRequestDao closeReqDao;
	public AccountCloseRequestService() {
		closeReqDao = new AccountCloseRequestDao();
	}
	
	public boolean saveReq(AccountCloseRequest req) {
		return closeReqDao.saveRequest(req);
	}
}
