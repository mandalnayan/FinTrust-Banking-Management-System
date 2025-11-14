package com.fintrust.service;

import org.zkoss.zhtml.Messagebox;

import com.fintrust.dao.NomineeDao;
import com.fintrust.model.Nominee;


public class NomineeServiceImp implements NomineeService{
	private final NomineeDao nomineeDao;
	public NomineeServiceImp() {
		nomineeDao = new NomineeDao();
	}

	public boolean saveNominee(Nominee nominee) {
		return nomineeDao.createNominee(nominee);
	}
	
	public boolean isPresentNominee(long nomineeId) {
		Nominee nominee = nomineeDao.getNominee(nomineeId);
		if(nominee != null) {
			return true;
		}
		return false;
	}
}
