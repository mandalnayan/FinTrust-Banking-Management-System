package com.fintrust.dao;

import java.sql.SQLException;
import java.util.List;

import com.fintrust.model.Bank;

public interface BankDAO {

    long create(Bank bank) throws SQLException;

    Bank findById(long bankId) throws SQLException;

    List<Bank> findAll() throws SQLException;

    boolean update(Bank bank) throws SQLException;

    boolean delete(long bankId) throws SQLException;
}

