package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.BeneficiaryDAO;
import com.fintrust.dao.impl.BeneficiaryDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Beneficiary;

/**
 * Service class to handle operations related to user beneficiaries.
 * <p>
 * Provides functionality to add new beneficiaries and fetch the list of
 * beneficiaries for the current user. Integrates with {@link BeneficiaryDAO} for
 * database operations and {@link DBConnection} for database connectivity.
 * <p>
 * Logging is included for auditing and error tracking, suitable for banking
 * applications.
 */
public class BeneficiaryService {

    private static final Logger logger = LogManager.getLogger(BeneficiaryService.class);

    /** Database connection used for DAO operations */
    private final Connection connection;

    /** DAO object for beneficiary database operations */
    private final BeneficiaryDAO beneficiaryDAO;

    /**
     * Default constructor that initializes the database connection and DAO.
     */
    public BeneficiaryService() {
        this.connection = DBConnection.getConnection();
        this.beneficiaryDAO = new BeneficiaryDAOImpl(connection);
        logger.info("BeneficiaryService initialized with database connection.");
    }

    /**
     * Saves a new beneficiary for the current user.
     *
     * @param beneficiary the {@link Beneficiary} object to be saved
     * @return {@code true} if the beneficiary was successfully saved, {@code false} otherwise
     */
    public boolean save(Beneficiary beneficiary) {
        try {
            boolean success = beneficiaryDAO.create(beneficiary) != -1L;
            if (success) {
                logger.info("Beneficiary added successfully for account: {}", beneficiary.getAccountNumber());
            } else {
                logger.warn("Failed to add beneficiary for account: {}", beneficiary.getAccountNumber());
            }
            return success;
        } catch (SQLException e) {
            logger.error("SQL error while saving beneficiary for account: {}", beneficiary.getAccountNumber(), e);
        }
        return false;
    }

    /**
     * Retrieves the list of beneficiaries for the currently logged-in user.
     *
     * @return a {@link List} of {@link Beneficiary} objects; empty list if none found
     */
    public List<Beneficiary> getBeneficiaries() {
        List<Beneficiary> list = new ArrayList<>();
        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");

        if (userId != null) {
            try {
                list = beneficiaryDAO.findByUserId(userId);
                logger.info("Fetched {} beneficiaries for userId: {}", list.size(), userId);
            } catch (SQLException e) {
                logger.error("SQL error while fetching beneficiaries for userId: {}", userId, e);
            }
        } else {
            logger.warn("No user_id found in session. Cannot fetch beneficiaries.");
        }

        return list;
    }
}
