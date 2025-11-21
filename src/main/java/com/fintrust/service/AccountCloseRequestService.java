package com.fintrust.service;

import com.fintrust.dao.AccountCloseRequestDao;
import com.fintrust.model_copy.AccountCloseRequest;

public class AccountCloseRequestService {
	AccountCloseRequestDao closeReqDao;
	public AccountCloseRequestService() {
		closeReqDao = new AccountCloseRequestDao();
	}
	
	public boolean saveReq(AccountCloseRequest req) {
		return closeReqDao.saveRequest(req);
	}
}
