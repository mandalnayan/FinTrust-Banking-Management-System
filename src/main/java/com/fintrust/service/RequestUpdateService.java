package com.fintrust.service;

import com.fintrust.dao.impl.AccountUpdateRequestDao;
import com.fintrust.model.AccountUpdateRequest;

public class RequestUpdateService {
	public boolean updateRequest(AccountUpdateRequest req) {
		AccountUpdateRequestDao aur_obj = new AccountUpdateRequestDao();
		Boolean isRequestExists = aur_obj.isRequestExists(req.getAccountNo());
		
		if (isRequestExists == null) {
			System.out.println("Server issues. Failed to update request..");
		} else if(!isRequestExists) {
			return new AccountUpdateRequestDao().save(req);
		}
		return false;
	}
}
