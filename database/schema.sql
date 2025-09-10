-- PayMyBuddy Database Schema
-- Complete setup script for MySQL Workbench
-- Version: 1.0
-- Date: 2025-08-29

-- =================================================
-- 1. DATABASE CREATION AND CONFIGURATION
-- =================================================

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS paymybuddy 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE paymybuddy;

-- Set SQL mode for better compatibility
SET SQL_MODE = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- =================================================
-- 2. DROP EXISTING TABLES (for clean reinstall)
-- =================================================

-- Drop tables in correct order (respecting foreign key dependencies)
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS connections;
DROP TABLE IF EXISTS users;

-- =================================================
-- 3. TABLE CREATION
-- =================================================

-- Table: users
-- Stores all user information and account balances
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00 CHECK (balance >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for performance
    INDEX idx_email (email),
    INDEX idx_last_name (last_name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB COMMENT='User accounts and profile information';

-- Table: connections
-- Manages friend relationships between users (bidirectional)
CREATE TABLE connections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Prevent duplicate connections and self-connections
    UNIQUE KEY unique_connection (user_id, friend_id),
    CONSTRAINT chk_no_self_connection CHECK (user_id != friend_id),
    
    -- Indexes for performance
    INDEX idx_user_id (user_id),
    INDEX idx_friend_id (friend_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB COMMENT='Friend connections between users';

-- Table: transactions
-- Records all money transfers between users
CREATE TABLE transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    description VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    
    -- Foreign key constraints
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE RESTRICT,
    
    -- Prevent self-transfers
    CONSTRAINT chk_no_self_transfer CHECK (sender_id != receiver_id),
    
    -- Indexes for performance
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_status (status),
    INDEX idx_sender_date (sender_id, transaction_date),
    INDEX idx_receiver_date (receiver_id, transaction_date)
) ENGINE=InnoDB COMMENT='Money transfer transactions between users';

-- =================================================
-- 4. STORED PROCEDURES FOR BUSINESS LOGIC
-- =================================================

DELIMITER //

-- Procedure: Transfer money between users
CREATE PROCEDURE TransferMoney(
    IN p_sender_id BIGINT,
    IN p_receiver_id BIGINT,
    IN p_amount DECIMAL(10,2),
    IN p_description VARCHAR(255),
    OUT p_transaction_id BIGINT,
    OUT p_status VARCHAR(20)
)
BEGIN
    DECLARE v_sender_balance DECIMAL(10,2);
    DECLARE v_error_message VARCHAR(255);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        GET DIAGNOSTICS CONDITION 1 v_error_message = MESSAGE_TEXT;
        SET p_status = CONCAT('ERROR: ', v_error_message);
        SET p_transaction_id = -1;
    END;

    START TRANSACTION;
    
    -- Check sender balance
    SELECT balance INTO v_sender_balance FROM users WHERE id = p_sender_id FOR UPDATE;
    
    IF v_sender_balance >= p_amount THEN
        -- Update balances
        UPDATE users SET balance = balance - p_amount WHERE id = p_sender_id;
        UPDATE users SET balance = balance + p_amount WHERE id = p_receiver_id;
        
        -- Create transaction record
        INSERT INTO transactions (sender_id, receiver_id, amount, description, status)
        VALUES (p_sender_id, p_receiver_id, p_amount, p_description, 'COMPLETED');
        
        SET p_transaction_id = LAST_INSERT_ID();
        SET p_status = 'SUCCESS';
        
        COMMIT;
    ELSE
        SET p_status = 'INSUFFICIENT_FUNDS';
        SET p_transaction_id = -1;
        ROLLBACK;
    END IF;
END//

DELIMITER ;

-- =================================================
-- 5. VIEWS FOR COMMON QUERIES
-- =================================================

-- View: User transaction history
CREATE VIEW user_transaction_history AS
SELECT 
    t.id,
    t.transaction_date,
    CASE 
        WHEN t.sender_id = u.id THEN 'SENT'
        ELSE 'RECEIVED'
    END as transaction_type,
    CASE 
        WHEN t.sender_id = u.id THEN CONCAT(r.first_name, ' ', r.last_name)
        ELSE CONCAT(s.first_name, ' ', s.last_name)
    END as contact_name,
    CASE 
        WHEN t.sender_id = u.id THEN r.email
        ELSE s.email
    END as contact_email,
    t.amount,
    t.description,
    t.status,
    u.id as user_id
FROM transactions t
JOIN users u ON (u.id = t.sender_id OR u.id = t.receiver_id)
JOIN users s ON s.id = t.sender_id
JOIN users r ON r.id = t.receiver_id;

-- View: User connections with friend details
CREATE VIEW user_connections_detailed AS
SELECT 
    c.id as connection_id,
    c.user_id,
    CONCAT(u.first_name, ' ', u.last_name) as user_name,
    u.email as user_email,
    c.friend_id,
    CONCAT(f.first_name, ' ', f.last_name) as friend_name,
    f.email as friend_email,
    c.created_at
FROM connections c
JOIN users u ON u.id = c.user_id
JOIN users f ON f.id = c.friend_id;

-- =================================================
-- 6. TRIGGERS FOR DATA INTEGRITY
-- =================================================

DELIMITER //

-- Trigger: Update user's updated_at timestamp
CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

-- Trigger: Validate transaction amounts
CREATE TRIGGER trg_validate_transaction
BEFORE INSERT ON transactions
FOR EACH ROW
BEGIN
    IF NEW.amount <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Transaction amount must be positive';
    END IF;
END//

DELIMITER ;

-- =================================================
-- 7. SECURITY SETTINGS
-- =================================================

-- Create application user (run this as admin)
-- CREATE USER IF NOT EXISTS 'paymybuddy_app'@'localhost' IDENTIFIED BY 'SecurePassword123!';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON paymybuddy.* TO 'paymybuddy_app'@'localhost';
-- GRANT EXECUTE ON PROCEDURE paymybuddy.TransferMoney TO 'paymybuddy_app'@'localhost';
-- FLUSH PRIVILEGES;

-- =================================================
-- 8. SAMPLE DATA FOR TESTING
-- =================================================

-- Sample users (passwords are BCrypt encoded 'password123')
INSERT INTO users (email, password, first_name, last_name, balance) VALUES
('john.doe@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'John', 'Doe', 500.00),
('jane.smith@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Jane', 'Smith', 750.50),
('bob.wilson@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Bob', 'Wilson', 300.25),
('alice.brown@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Alice', 'Brown', 1000.00),
('mike.johnson@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Mike', 'Johnson', 250.75);

-- Sample connections (bidirectional friendships)
INSERT INTO connections (user_id, friend_id) VALUES
(1, 2), (2, 1), -- John ↔ Jane
(1, 3), (3, 1), -- John ↔ Bob
(2, 4), (4, 2), -- Jane ↔ Alice
(3, 4), (4, 3), -- Bob ↔ Alice
(1, 5), (5, 1), -- John ↔ Mike
(2, 5), (5, 2); -- Jane ↔ Mike

-- Sample transactions
INSERT INTO transactions (sender_id, receiver_id, amount, description, status) VALUES
(1, 2, 50.00, 'Remboursement restaurant', 'COMPLETED'),
(2, 1, 25.00, 'Partage Uber', 'COMPLETED'),
(1, 3, 100.00, 'Cadeau anniversaire', 'COMPLETED'),
(4, 2, 75.50, 'Courses partagées', 'COMPLETED'),
(3, 4, 30.00, 'Cinéma', 'COMPLETED'),
(5, 1, 45.25, 'Remboursement essence', 'COMPLETED'),
(2, 5, 60.00, 'Dîner restaurant', 'PENDING');

-- =================================================
-- 9. VERIFICATION QUERIES
-- =================================================

-- Verify table creation
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    CREATE_TIME
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'paymybuddy';

-- Verify sample data
SELECT 'Users' as TableName, COUNT(*) as RecordCount FROM users
UNION ALL
SELECT 'Connections', COUNT(*) FROM connections
UNION ALL
SELECT 'Transactions', COUNT(*) FROM transactions;

-- Show user balances
SELECT 
    CONCAT(first_name, ' ', last_name) as full_name,
    email,
    balance
FROM users
ORDER BY balance DESC;

COMMIT;
