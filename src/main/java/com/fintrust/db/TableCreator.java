package com.fintrust.db;

import java.sql.Connection;
import java.sql.Statement;

public class TableCreator {

    public static void createAllTables() {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();

            // USERS TABLE
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(120) UNIQUE NOT NULL,
                    phone VARCHAR(20) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role ENUM('user','admin','super_admin') NOT NULL DEFAULT 'user',
                    status ENUM('active','inactive','blocked') NOT NULL DEFAULT 'active',
                    created_at DATETIME,
                    updated_at DATETIME
                )
            """);

            // USER DETAILS TABLE
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user_details (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    full_name VARCHAR(150) NOT NULL,
                    gender ENUM('male','female','other'),
                    dob DATE,
                    country VARCHAR(50),
                    state VARCHAR(50),
                    district VARCHAR(50),
                    city VARCHAR(50),
                    pincode VARCHAR(10),
                    created_at DATETIME,
                    updated_at DATETIME,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // ACCOUNTS TABLE
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    account_number VARCHAR(20) UNIQUE NOT NULL,
                    account_type ENUM('savings','current','salary') NOT NULL,
                    balance DECIMAL(15,2) DEFAULT 0,
                    status ENUM('active','inactive','frozen','closed') DEFAULT 'active',
                    created_at DATETIME,
                    updated_at DATETIME,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // TRANSACTIONS TABLE
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    account_id INT NOT NULL,
                    reference_id VARCHAR(40) UNIQUE,
                    txn_type ENUM('credit','debit') NOT NULL,
                    amount DECIMAL(15,2) NOT NULL,
                    description VARCHAR(255),
                    mode ENUM('online','upi','card','neft','rtgs','imps','cash'),
                    status ENUM('pending','completed','failed','reversed') DEFAULT 'pending',
                    created_at DATETIME,
                    FOREIGN KEY (account_id) REFERENCES accounts(id)
                )
            """);

            // BENEFICIARIES TABLE
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS beneficiaries (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    name VARCHAR(120),
                    account_number VARCHAR(20),
                    bank_name VARCHAR(120),
                    ifsc_code VARCHAR(20),
                    created_at DATETIME,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // CARDS TABLE
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cards (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    account_id INT NOT NULL,
                    card_number VARCHAR(20) UNIQUE NOT NULL,
                    card_type ENUM('debit','credit','prepaid'),
                    provider ENUM('visa','mastercard','rupay'),
                    expiry_month INT,
                    expiry_year INT,
                    cvv VARCHAR(4),
                    status ENUM('active','blocked','expired') DEFAULT 'active',
                    created_at DATETIME,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (account_id) REFERENCES accounts(id)
                )
            """);

            // LOANS TABLE
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS loans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT,
                    loan_type ENUM('home','personal','car','education'),
                    amount DECIMAL(15,2),
                    interest_rate DECIMAL(5,2),
                    tenure_months INT,
                    status ENUM('active','closed','defaulted'),
                    created_at DATETIME,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // SUPPORT TICKETS TABLE
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS support_tickets (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT,
                    subject VARCHAR(150),
                    message TEXT,
                    priority ENUM('low','medium','high'),
                    status ENUM('open','in_progress','closed'),
                    created_at DATETIME,
                    updated_at DATETIME,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // ADMIN LOGS TABLE
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS admin_logs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    admin_id INT,
                    action VARCHAR(255),
                    ip_address VARCHAR(40),
                    user_agent VARCHAR(255),
                    created_at DATETIME,
                    FOREIGN KEY (admin_id) REFERENCES users(id)
                )
            """);

            System.out.println("All tables created successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
