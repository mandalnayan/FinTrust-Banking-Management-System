package com.fintrust.db;

import java.sql.Connection;
import java.sql.Statement;

public class TableCreator {

	public static void createAllTables() {
		try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement()) {

			// Enable strict SQL mode
			st.execute("SET SESSION sql_mode = 'STRICT_ALL_TABLES';");

			// -----------------------------
			// 1) banks
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS banks (
					        bank_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        bank_name VARCHAR(120) NOT NULL,
					        branch_name VARCHAR(120) NOT NULL,
					        ifsc_code CHAR(11) NOT NULL UNIQUE,
					        support_email VARCHAR(120),
					        support_phone VARCHAR(20),
					        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 2) users
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS users (
					        user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        full_name VARCHAR(120) NOT NULL,
					        email VARCHAR(120) NOT NULL UNIQUE,
					        phone VARCHAR(20),
					        password_hash VARCHAR(255) NOT NULL,
					        role ENUM('ROLE_USER','ROLE_ADMIN','ROLE_SUPER_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
					        status ENUM('active','inactive','blocked') NOT NULL DEFAULT 'active',
					        kycStatus ENUM('pendnig','upated','expired') NOT NULL DEFAULT 'pending',
					        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
					        INDEX idx_users_email(email),
					        INDEX idx_users_phone(phone)
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 3) accounts
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS accounts (
					        account_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        user_id BIGINT UNSIGNED NOT NULL,
					        bank_id BIGINT UNSIGNED NOT NULL,
					        account_number CHAR(16) NOT NULL UNIQUE,
					        account_type ENUM('savings','current','salary','fixed_deposit') NOT NULL,
					        balance DECIMAL(18,2) NOT NULL DEFAULT 0.00,
					        currency VARCHAR(10) NOT NULL DEFAULT 'INR',
					        status ENUM('active','inactive','frozen','closed') DEFAULT 'active',
					        opened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
					        CONSTRAINT fk_accounts_user FOREIGN KEY (user_id)
					            REFERENCES users(user_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT,
					        CONSTRAINT fk_accounts_bank FOREIGN KEY (bank_id)
					            REFERENCES banks(bank_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT,
					        INDEX idx_accounts_user(user_id)
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 4) user_details
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS user_details (
					        details_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        user_id BIGINT UNSIGNED NOT NULL,
					        primary_account_id BIGINT UNSIGNED NULL,
					        gender ENUM('male','female','other'),
					        dob DATE,
					        aadhaar_masked VARBINARY(255),
					        pan_masked VARBINARY(255),
					        country VARCHAR(50),
					        state VARCHAR(50),
					        district VARCHAR(50),
					        city VARCHAR(50),
					        pincode VARCHAR(10),
					        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
					        CONSTRAINT fk_ud_user FOREIGN KEY (user_id)
					            REFERENCES users(user_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT,
					        CONSTRAINT fk_userdetails_primary_account FOREIGN KEY (primary_account_id)
					            REFERENCES accounts(account_id)
					            ON UPDATE CASCADE
					            ON DELETE SET NULL,
					        UNIQUE INDEX ux_ud_userid(user_id)
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");
			// -----------------------------
			// 5) card
			// -----------------------------
			st.execute("""
					                CREATE TABLE IF NOT EXISTS cards (
					    card_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

					    user_id BIGINT UNSIGNED NOT NULL,
					    account_id BIGINT UNSIGNED NOT NULL,

					    -- Masked card number (example: XXXX-XXXX-XXXX-1234)
					    card_number_masked VARCHAR(24) NOT NULL,

					    -- Last 4 digits stored separately (optional)
					    last4 CHAR(4) NOT NULL,

					    -- Encrypted PIN, never store raw PIN
					    pin_hash VARCHAR(255) NOT NULL,

					    -- Bank NEVER stores CVV (PCI-DSS restriction)
					    -- cvv is validated at gateway only
					    -- (REMOVED from design)

					    issued_date DATE NOT NULL DEFAULT (CURRENT_DATE),

					    expiry_date DATE NOT NULL,

					    card_status ENUM('active', 'blocked', 'expired', 'hotlisted')
					        NOT NULL DEFAULT 'active',

					    maximum_limit DECIMAL(12,2) DEFAULT 50000.00,

					    provider ENUM('visa', 'mastercard', 'rupay', 'amex')
					        DEFAULT 'visa',

					    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

					    CONSTRAINT fk_cards_user
					        FOREIGN KEY (user_id) REFERENCES users(user_id)
					        ON UPDATE CASCADE
					        ON DELETE RESTRICT,

					    CONSTRAINT fk_cards_account
					        FOREIGN KEY (account_id) REFERENCES accounts(account_id)
					        ON UPDATE CASCADE
					        ON DELETE RESTRICT
					) ENGINE=InnoDB
					  DEFAULT CHARSET=utf8mb4
					  COLLATE=utf8mb4_unicode_ci;

					            """);

			// -----------------------------
			// 6) beneficiaries
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS beneficiaries (
					        beneficiary_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        user_id BIGINT UNSIGNED NOT NULL,
					        name VARCHAR(120) NOT NULL,
					        account_number CHAR(16) NOT NULL,
					        bank_name VARCHAR(150),
					        ifsc_code CHAR(11),
					        added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        CONSTRAINT fk_benef_user FOREIGN KEY (user_id)
					            REFERENCES users(user_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT,
					        INDEX idx_benef_user(user_id)
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 6) transactions
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS transactions (
					        transaction_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        account_id BIGINT UNSIGNED NOT NULL,
					        related_account_id BIGINT UNSIGNED DEFAULT NULL,
					        beneficiary_id BIGINT UNSIGNED DEFAULT NULL,
					        txn_reference VARCHAR(64) UNIQUE,
					        txn_type ENUM('credit','debit') NOT NULL,
					        mode ENUM('online','upi','card','neft','rtgs','imps','cash') NOT NULL DEFAULT 'online',
					        amount DECIMAL(18,2) NOT NULL,
					        balance_after DECIMAL(18,2),
					        description VARCHAR(512),
					        status ENUM('pending','completed','failed','reversed') NOT NULL DEFAULT 'pending',
					        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        CONSTRAINT fk_tx_account FOREIGN KEY (account_id)
					            REFERENCES accounts(account_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT,
					        CONSTRAINT fk_tx_related_account FOREIGN KEY (related_account_id)
					            REFERENCES accounts(account_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT,
					        CONSTRAINT fk_tx_beneficiary FOREIGN KEY (beneficiary_id)
					            REFERENCES beneficiaries(beneficiary_id)
					            ON UPDATE CASCADE
					            ON DELETE SET NULL,
					        INDEX idx_tx_account(account_id),
					        INDEX idx_tx_related_account(related_account_id),
					        INDEX idx_tx_beneficiary(beneficiary_id)
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 7) cards
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS cards (
					        card_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        user_id BIGINT UNSIGNED NOT NULL,
					        account_id BIGINT UNSIGNED,
					        card_number_masked VARBINARY(255) NOT NULL,
					        card_bin CHAR(8),
					        card_type ENUM('debit','credit','prepaid') NOT NULL,
					        provider ENUM('visa','mastercard','rupay','amex'),
					        expiry_date DATE,
					        status ENUM('active','blocked','expired') DEFAULT 'active',
					        issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        CONSTRAINT fk_cards_user FOREIGN KEY (user_id)
					            REFERENCES users(user_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT,
					        CONSTRAINT fk_cards_account FOREIGN KEY (account_id)
					            REFERENCES accounts(account_id)
					            ON UPDATE CASCADE
					            ON DELETE SET NULL
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 8) loans
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS loans (
					        loan_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        user_id BIGINT UNSIGNED NOT NULL,
					        loan_type ENUM('home','personal','education','vehicle','business') DEFAULT 'personal',
					        principal_amount DECIMAL(18,2) NOT NULL,
					        interest_rate DECIMAL(5,2),
					        tenure_months INT,
					        status ENUM('applied','approved','active','closed','defaulted') DEFAULT 'applied',
					        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        approved_at TIMESTAMP NULL,
					        CONSTRAINT fk_loans_user FOREIGN KEY (user_id)
					            REFERENCES users(user_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 9) loan_payments
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS loan_payments (
					        payment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        loan_id BIGINT UNSIGNED NOT NULL,
					        amount DECIMAL(18,2) NOT NULL,
					        payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        method ENUM('neft','rtgs','imps','upi','card','cash') DEFAULT 'neft',
					        status ENUM('success','failed','pending') DEFAULT 'success',
					        CONSTRAINT fk_lp_loan FOREIGN KEY (loan_id)
					            REFERENCES loans(loan_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 10) support_tickets
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS support_tickets (
					        ticket_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        user_id BIGINT UNSIGNED NOT NULL,
					        subject VARCHAR(200) NOT NULL,
					        message TEXT NOT NULL,
					        priority ENUM('low','medium','high') DEFAULT 'medium',
					        status ENUM('open','in_progress','resolved','closed') DEFAULT 'open',
					        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
					        CONSTRAINT fk_tickets_user FOREIGN KEY (user_id)
					            REFERENCES users(user_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 11) admin_logs
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS admin_logs (
					        log_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        admin_id BIGINT UNSIGNED NOT NULL,
					        action VARCHAR(512) NOT NULL,
					        metadata JSON,
					        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        CONSTRAINT fk_adminlogs_admin FOREIGN KEY (admin_id)
					            REFERENCES users(user_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			// -----------------------------
			// 12) login_history
			// -----------------------------
			st.execute("""
					    CREATE TABLE IF NOT EXISTS login_history (
					        login_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
					        user_id BIGINT UNSIGNED NOT NULL,
					        ip_address VARCHAR(45),
					        device_info VARCHAR(255),
					        status ENUM('success','failed') DEFAULT 'success',
					        occurred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
					        CONSTRAINT fk_lh_user FOREIGN KEY (user_id)
					            REFERENCES users(user_id)
					            ON UPDATE CASCADE
					            ON DELETE RESTRICT
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
					""");

			System.out.println("✅ All banking tables created successfully for Workbench!");

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("❌ Error creating tables: " + e.getMessage());
		}
	}
}
