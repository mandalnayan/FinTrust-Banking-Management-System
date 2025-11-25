package com.fintrust.service;

import java.sql.SQLException;

import org.zkoss.zhtml.Messagebox;

import com.fintrust.model.Nominee;

import com.fintrust.dao.impl.NomineeDAO;


public class NomineeServiceImp implements NomineeService{
	private final NomineeDAO nomineeDAO;
	public NomineeServiceImp() {
		nomineeDAO = new NomineeDAO();
	}

	public Long saveNominee(Nominee nominee) {
		try {
			return nomineeDAO.createNominee(nominee);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1l;
	}
	
	public boolean isPresentNominee(long nomineeId) {
		Nominee nominee = nomineeDAO.getNominee(nomineeId);
		if(nominee != null) {
			return true;
		}
		return false;
	}
}
