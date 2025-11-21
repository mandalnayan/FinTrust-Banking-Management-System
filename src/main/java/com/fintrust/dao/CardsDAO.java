package com.fintrust.dao;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing user cards (debit/credit/prepaid) in the banking system.
 * <p>
 * All methods follow banking-grade secure JDBC standards.
 */
public interface CardsDAO {

    /**
     * Issues a new card for a user.
     *
     * @param userId         ID of the user
     * @param accountId      linked account ID (nullable for credit cards)
     * @param cardNumberMasked masked card number
     * @param cardBin        first 6-8 digits of card
     * @param cardType       'debit','credit','prepaid'
     * @param provider       'visa','mastercard','rupay','amex'
     * @param expiryDate     card expiry date
     * @param status         card status ('active','blocked','expired')
     * @return generated card_id
     * @throws SQLException if database operation fails
     */
    long create(long userId, Long accountId, String cardNumberMasked,
                String cardBin, String cardType, String provider,
                java.sql.Date expiryDate, String status) throws SQLException;

    /**
     * Finds a card by card_id.
     *
     * @param cardId card ID
     * @return Map representing the card record, or null
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findById(long cardId) throws SQLException;

    /**
     * Finds all cards of a user.
     *
     * @param userId user ID
     * @return list of card records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findByUserId(long userId) throws SQLException;

    /**
     * Retrieves all cards in the system.
     *
     * @return list of card records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Updates card information.
     *
     * @param cardId  card ID
     * @param status  card status ('active','blocked','expired')
     * @param expiryDate new expiry date
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean update(long cardId, String status, java.sql.Date expiryDate) throws SQLException;

    /**
     * Deletes a card record.
     *
     * @param cardId card ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long cardId) throws SQLException;
}
