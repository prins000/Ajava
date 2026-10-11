-- ====================================================================
-- Authentica Scam & Cyber Threat Detection Platform
-- Database Schema Definition (DDL)
-- Compatible with MySQL 8.0+ and PostgreSQL / H2
-- Course: Advanced Java Programming (BE05000131) - GTU Semester V
-- ====================================================================

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    trust_score INT DEFAULT 85,
    risk_level VARCHAR(50) DEFAULT 'LOW',
    is_email_verified BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Analyses Table (Threat Scan Audits)
CREATE TABLE IF NOT EXISTS analyses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'COMPLETED',
    trust_score INT,
    risk_level VARCHAR(50),
    confidence DOUBLE,
    input_target VARCHAR(2000),
    summary VARCHAR(4000),
    details_json TEXT,
    processing_time_ms BIGINT DEFAULT 0,
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_analysis_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 3. Community Reports Table
CREATE TABLE IF NOT EXISTS community_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    reporter_name VARCHAR(255) DEFAULT 'Anonymous',
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    target_identifier VARCHAR(1000),
    additional_info VARCHAR(2000),
    tags VARCHAR(500),
    city VARCHAR(100),
    state VARCHAR(100),
    upvotes INT DEFAULT 0,
    downvotes INT DEFAULT 0,
    view_count INT DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 4. Fraud Numbers Registry
CREATE TABLE IF NOT EXISTS fraud_numbers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(50) NOT NULL UNIQUE,
    risk_score INT DEFAULT 90,
    category VARCHAR(100) DEFAULT 'Financial Fraud',
    report_count INT DEFAULT 1,
    carrier VARCHAR(100),
    description VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Fraud URLs Registry
CREATE TABLE IF NOT EXISTS fraud_urls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(1000) NOT NULL UNIQUE,
    domain VARCHAR(255) NOT NULL,
    risk_score INT DEFAULT 95,
    category VARCHAR(100) DEFAULT 'Phishing',
    phishing_indicators VARCHAR(1000),
    report_count INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Fraud QR & UPI Codes Registry
CREATE TABLE IF NOT EXISTS fraud_qrs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    qr_content VARCHAR(1000) NOT NULL,
    qr_type VARCHAR(50) DEFAULT 'UPI',
    risk_score INT DEFAULT 90,
    upi_handle VARCHAR(255),
    merchant_name VARCHAR(255),
    flags VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Protected Family Contacts
CREATE TABLE IF NOT EXISTS protected_contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    contact_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    relationship VARCHAR(100),
    is_alert_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contact_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 8. Support Tickets
CREATE TABLE IF NOT EXISTS support_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    user_email VARCHAR(255),
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ticket_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 9. User Feedbacks
CREATE TABLE IF NOT EXISTS feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    user_email VARCHAR(255),
    category VARCHAR(100) DEFAULT 'general',
    rating INT DEFAULT 5,
    comment VARCHAR(2000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Whitelist Registry (Official Portals / Safe Numbers)
CREATE TABLE IF NOT EXISTS whitelists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    item_value VARCHAR(500) NOT NULL,
    reason VARCHAR(500),
    added_by VARCHAR(100) DEFAULT 'SYSTEM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Application Settings
CREATE TABLE IF NOT EXISTS app_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    description VARCHAR(255),
    category VARCHAR(100) DEFAULT 'system',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Fast Query Performance
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_analysis_user ON analyses(user_id);
CREATE INDEX idx_analysis_type ON analyses(type);
CREATE INDEX idx_report_status ON community_reports(status);
CREATE INDEX idx_fraud_phone ON fraud_numbers(phone_number);
CREATE INDEX idx_fraud_domain ON fraud_urls(domain);
