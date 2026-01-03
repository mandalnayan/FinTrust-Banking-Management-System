package com.fintrust.dao;

import java.sql.SQLException;
import java.util.List;

import com.fintrust.model.Branch;

public interface BranchDAO {

    long create(Branch branch) throws SQLException;

    Branch findById(long bankId) throws SQLException;
    
    Branch findByBranchName(String branchName) throws SQLException;

    List<Branch> findAll() throws SQLException;

    boolean update(Branch branch) throws SQLException;

    boolean delete(long bankId) throws SQLException;
}

