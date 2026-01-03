package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.BeneficiaryDAO;
import com.fintrust.dao.impl.BeneficiaryDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Beneficiary;

public class BeneficiaryService {
	Connection connection;
	BeneficiaryDAO beneficiaryDAO;

	public BeneficiaryService() {
		connection = DBConnection.getConnection();
		beneficiaryDAO = new BeneficiaryDAOImpl(connection);
	}

	public boolean save(Beneficiary beneficiary) {
		try {
			return beneficiaryDAO.create(beneficiary) != -1l;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<Beneficiary> getBeneficiaries() {
		List<Beneficiary> list = new ArrayList<>();
		Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
		try {
			if (userId != null) {
				list = beneficiaryDAO.findByUserId(userId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
