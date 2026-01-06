package com.fintrust.service;

import com.fintrust.model.AccountCloseRequest;
import com.fintrust.dao.impl.AccountCloseRequestDao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service class to handle operations related to account closure requests.
 * <p>
 * This class interacts with {@link AccountCloseRequestDao} to perform database
 * operations like checking for existing requests and saving new account closure requests.
 * <p>
 * It is designed for use in a banking application and ensures proper logging of events.
 */
public class AccountCloseRequestService {

    private static final Logger logger = LogManager.getLogger(AccountCloseRequestService.class);

    /** DAO object for account closure request database operations */
    private final AccountCloseRequestDao closeReqDao;

    /**
     * Default constructor initializing the DAO.
     */
    public AccountCloseRequestService() {
        this.closeReqDao = new AccountCloseRequestDao();
        logger.info("AccountCloseRequestService initialized.");
    }

    /**
     * Saves an account closure request if it does not already exist.
     *
     * @param req the {@link AccountCloseRequest} object containing account closure details
     * @return {@code true} if the request was successfully saved, {@code false} otherwise
     */
    public boolean saveReq(AccountCloseRequest req) {
        Boolean isExist = closeReqDao.isRequestExist(req.getAccountNo());

        if (isExist == null) {
            // Log database error
            logger.error("Database error while checking if account closure request exists for account: {}", req.getAccountNo());
        } else if (!isExist) {
            logger.info("Saving new account closure request for account: {}", req.getAccountNo());
            return closeReqDao.saveRequest(req);
        } else {
            logger.warn("Account closure request already exists for account: {}", req.getAccountNo());
        }

        return false;
    }
}
