-- ====================================================================
-- Authentica Scam & Cyber Threat Detection Platform
-- Seed Data Script (DML)
-- Contains verified admin, users, threat blacklists, and community alerts
-- ====================================================================

-- 1. Default Users (Passwords hashed using BCrypt: Admin@1234 & User@1234)
INSERT INTO users (name, email, password, phone, role, trust_score, risk_level, is_email_verified, is_active)
VALUES 
('Security Admin', 'admin@authentica.io', '$2a$10$oY7bC1w09xGvY0K4nE2m2.f8XJc3gM1.17Ua6Yk4L5N2O8p9Q1R2S', '+919800000001', 'ROLE_ADMIN', 99, 'SAFE', TRUE, TRUE),
('Aarav Sharma', 'user@authentica.io', '$2a$10$s7jP0xW.9yHwZ1L5oF3n3.g9YKd4hN2.28Vb7Zl5M6O3P9q0R2S3T', '+919800000002', 'ROLE_USER', 85, 'LOW', TRUE, TRUE),
('Priya Patel', 'priya@example.com', '$2a$10$s7jP0xW.9yHwZ1L5oF3n3.g9YKd4hN2.28Vb7Zl5M6O3P9q0R2S3T', '+919800000003', 'ROLE_USER', 78, 'LOW', TRUE, TRUE);

-- 2. Official Whitelists
INSERT INTO whitelists (type, item_value, reason, added_by)
VALUES
('DOMAIN', 'cybercrime.gov.in', 'Official Indian National Cyber Crime Reporting Portal', 'SYSTEM'),
('DOMAIN', 'onlinesbi.sbi', 'Official State Bank of India portal', 'SYSTEM'),
('DOMAIN', 'uidai.gov.in', 'Official UIDAI Aadhaar portal', 'SYSTEM'),
('NUMBER', '1930', 'National Cyber Financial Fraud Helpline', 'SYSTEM'),
('NUMBER', '1909', 'TRAI Do-Not-Disturb and Spam Registry', 'SYSTEM');

-- 3. Verified Fraud Numbers
INSERT INTO fraud_numbers (phone_number, risk_score, category, report_count, carrier, description)
VALUES
('+919876543210', 95, 'Fake Bank KYC Scam', 14, 'Airtel Spoofer', 'Impersonating SBI manager claiming debit card blocked. Demands AnyDesk installation and OTP.'),
('+918888877777', 92, 'Electricity Disconnection Scam', 21, 'Jio Gateway', 'Automated SMS warning power disconnection tonight at 9:30 PM unless bill is cleared via unverified link.'),
('+919999911111', 88, 'Digital Arrest Extortion', 8, 'Unknown VoIP', 'Impersonates Mumbai Crime Branch regarding narcotics parcel found at FedEx customs.');

-- 4. Verified Fraud URLs
INSERT INTO fraud_urls (url, domain, risk_score, category, phishing_indicators, report_count)
VALUES
('http://secure-sbi-update-kyc.com/login', 'secure-sbi-update-kyc.com', 98, 'Phishing', 'Harvests NetBanking username, password, and profile password.', 19),
('http://electricity-bill-pay-quick.xyz/pay', 'electricity-bill-pay-quick.xyz', 94, 'Fake Payment Gateway', 'Suspicious .xyz TLD, spoofed utility payment interface.', 11),
('http://lottery-prize-winner-claim.top/win', 'lottery-prize-winner-claim.top', 89, 'Advance Fee Fraud', 'Counterfeit prize page requiring upfront fee to release winnings.', 7);

-- 5. Verified Fraud QRs
INSERT INTO fraud_qrs (qr_content, qr_type, risk_score, upi_handle, merchant_name, flags)
VALUES
('upi://pay?pa=refund-desk@ybl&pn=QuickRefundHelpdesk&tn=claim%20refund&am=5000', 'UPI', 95, 'refund-desk@ybl', 'QuickRefundHelpdesk', 'Deceptive collect request disguised as claim refund.');

-- 6. Verified Community Incident Reports
INSERT INTO community_reports (user_id, reporter_name, type, title, description, target_identifier, tags, city, state, upvotes, downvotes, view_count, status)
VALUES
(2, 'Rohit Patel', 'SMS', 'Urgent Electricity Disconnection Threat SMS', 'Received SMS claiming electricity will be cut off tonight at 9:30 PM. Instructed to contact officer at 9876543210.', '+919876543210', 'Electricity Scam, Utility Threat, Urgent', 'Ahmedabad', 'Gujarat', 18, 0, 142, 'VERIFIED'),
(3, 'Pooja Shah', 'CALL', 'Digital Arrest & FedEx Parcel Extortion Call', 'Caller claiming to be Mumbai Crime Branch stating FedEx parcel with drugs intercepted. Demanded Skype video call.', '+919999911111', 'Digital Arrest, Police Impersonation, Skype', 'Surat', 'Gujarat', 24, 1, 210, 'VERIFIED'),
(2, 'Karan Mehta', 'URL', 'Counterfeit SBI Netbanking Phishing Site', 'Fake link received over WhatsApp asking to update PAN card details or account suspended in 24 hrs.', 'http://secure-sbi-update-kyc.com/login', 'Bank Phishing, SBI, PAN Card', 'Vadodara', 'Gujarat', 12, 0, 98, 'VERIFIED');

-- 7. App Settings
INSERT INTO app_settings (setting_key, setting_value, description, category)
VALUES
('system.version', '1.0.0-GTU-PBL', 'Platform version', 'system'),
('threat.auto_verify_upvotes', '5', 'Upvote threshold for auto-verifying community reports', 'threat_intel'),
('analysis.cache_enabled', 'true', 'Enable memory caching for threat reputation lookups', 'performance');
