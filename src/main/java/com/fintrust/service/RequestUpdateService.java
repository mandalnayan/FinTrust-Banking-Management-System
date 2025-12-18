package com.fintrust.service;

import java.sql.SQLException;

import com.fintrust.dao.BranchDAO;
import com.fintrust.dao.impl.AccountUpdateRequestDao;
import com.fintrust.dao.impl.BranchDao;
import com.fintrust.model.AccountUpdateRequest;
import com.fintrust.model.Branch;

public class RequestUpdateService {
	public boolean updateRequest(AccountUpdateRequest req) {
		AccountUpdateRequestDao aur_obj = new AccountUpdateRequestDao();
		Boolean isRequestExists = aur_obj.isRequestExists(req.getAccountNo());
		
		if (isRequestExists == null) {
			System.out.println("Server issues. Failed to update request..");
		} else if(!isRequestExists) {
			BranchDAO branchDAO = new BranchDao();
			try {
				Branch branch = branchDAO.findByBranchName(req.getNewBranchName());
				if (branch != null) {
					req.setBranchId(branch.getBranchId());
					return new AccountUpdateRequestDao().save(req);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return false;
	}
}
