package com.authentica.service;

import com.authentica.model.*;
import com.authentica.model.enums.*;
import com.authentica.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataInitializerService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FraudNumberRepository fraudNumberRepository;
    private final FraudURLRepository fraudURLRepository;
    private final FraudQRRepository fraudQRRepository;
    private final CommunityReportRepository communityReportRepository;
    private final WhitelistRepository whitelistRepository;
    private final AppSettingsRepository settingsRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializerService(UserRepository userRepository,
                                  FraudNumberRepository fraudNumberRepository,
                                  FraudURLRepository fraudURLRepository,
                                  FraudQRRepository fraudQRRepository,
                                  CommunityReportRepository communityReportRepository,
                                  WhitelistRepository whitelistRepository,
                                  AppSettingsRepository settingsRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.fraudNumberRepository = fraudNumberRepository;
        this.fraudURLRepository = fraudURLRepository;
        this.fraudQRRepository = fraudQRRepository;
        this.communityReportRepository = communityReportRepository;
        this.whitelistRepository = whitelistRepository;
        this.settingsRepository = settingsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedWhitelists();
        seedFraudIntelligence();
        seedCommunityReports();
        seedSettings();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@authentica.io")) {
            User admin = new User(
                    "Security Admin",
                    "admin@authentica.io",
                    passwordEncoder.encode("Admin@1234"),
                    "+919800000001",
                    Role.ROLE_ADMIN
            );
            admin.setTrustScore(99);
            admin.setRiskLevel(RiskLevel.SAFE);
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("user@authentica.io")) {
            User demoUser = new User(
                    "Aarav Sharma",
                    "user@authentica.io",
                    passwordEncoder.encode("User@1234"),
                    "+919800000002",
                    Role.ROLE_USER
            );
            demoUser.setTrustScore(85);
            demoUser.setRiskLevel(RiskLevel.LOW);
            userRepository.save(demoUser);
        }
    }

    private void seedWhitelists() {
        if (whitelistRepository.count() == 0) {
            whitelistRepository.save(new Whitelist(WhitelistType.DOMAIN, "cybercrime.gov.in", "Official Indian National Cyber Crime Reporting Portal", "SYSTEM"));
            whitelistRepository.save(new Whitelist(WhitelistType.DOMAIN, "onlinesbi.sbi", "Official State Bank of India portal", "SYSTEM"));
            whitelistRepository.save(new Whitelist(WhitelistType.DOMAIN, "uidai.gov.in", "Official UIDAI Aadhaar portal", "SYSTEM"));
            whitelistRepository.save(new Whitelist(WhitelistType.NUMBER, "1930", "National Cyber Financial Fraud Helpline", "SYSTEM"));
            whitelistRepository.save(new Whitelist(WhitelistType.NUMBER, "1909", "TRAI Do-Not-Disturb and Spam Registry", "SYSTEM"));
        }
    }

    private void seedFraudIntelligence() {
        if (fraudNumberRepository.count() == 0) {
            fraudNumberRepository.save(new FraudNumber("+919876543210", 95, "Fake Bank KYC Scam", 14, "Impersonating SBI manager claiming debit card blocked. Requests OTP and AnyDesk installation."));
            fraudNumberRepository.save(new FraudNumber("+918888877777", 92, "Electricity Disconnection Scam", 21, "Automated SMS stating electricity will be disconnected tonight at 9:30 PM unless bill is paid via link."));
            fraudNumberRepository.save(new FraudNumber("+919999911111", 88, "Digital Arrest Police Impersonation", 8, "Caller claims to be Mumbai Crime Branch regarding seized narcotics parcel from FedEx."));
        }

        if (fraudURLRepository.count() == 0) {
            fraudURLRepository.save(new FraudURL("http://secure-sbi-update-kyc.com/login", "secure-sbi-update-kyc.com", 98, "Phishing", "Spoofed SBI net banking portal designed to harvest user ID, password, and profile password."));
            fraudURLRepository.save(new FraudURL("http://electricity-bill-pay-quick.xyz/pay", "electricity-bill-pay-quick.xyz", 94, "Fake Payment Gateway", "Phishing payment gateway attempting to drain debit cards through counterfeit utility bill forms."));
            fraudURLRepository.save(new FraudURL("http://lottery-prize-winner-claim.top/win", "lottery-prize-winner-claim.top", 89, "Advance Fee Fraud", "Spam lottery page asking for processing fee to release 25 lakh INR prize money."));
        }

        if (fraudQRRepository.count() == 0) {
            fraudQRRepository.save(new FraudQR("upi://pay?pa=refund-desk@ybl&pn=QuickRefundHelpdesk&tn=claim%20refund&am=5000", "UPI", 95, "refund-desk@ybl", "QuickRefundHelpdesk", "Deceptive collect transaction labeled as 'claim refund' to steal Rs 5000 from victim."));
        }
    }

    private void seedCommunityReports() {
        if (communityReportRepository.count() == 0) {
            CommunityReport r1 = new CommunityReport();
            r1.setReporterName("Rohit Patel");
            r1.setType("SMS");
            r1.setTitle("Urgent Electricity Disconnection Threat SMS");
            r1.setDescription("Received message: 'Dear consumer, your electricity power will be disconnected tonight at 9:30 PM from electricity office because your previous month bill was not updated. Please immediately contact our electricity officer at 9876543210'. Total fraud!");
            r1.setTargetIdentifier("+919876543210");
            r1.setTags("Electricity Scam, Utility Threat, Urgent");
            r1.setCity("Ahmedabad");
            r1.setState("Gujarat");
            r1.setUpvotes(18);
            r1.setViewCount(142);
            r1.setStatus(ReportStatus.VERIFIED);
            communityReportRepository.save(r1);

            CommunityReport r2 = new CommunityReport();
            r2.setReporterName("Pooja Shah");
            r2.setType("CALL");
            r2.setTitle("Digital Arrest & FedEx Parcel Extortion Call");
            r2.setDescription("Got a call stating a parcel sent in my name to Taiwan was intercepted at Mumbai airport containing 5 passports and MDMA. Demanded to connect via Skype with fake police uniform.");
            r2.setTargetIdentifier("+919999911111");
            r2.setTags("Digital Arrest, Police Impersonation, Skype");
            r2.setCity("Surat");
            r2.setState("Gujarat");
            r2.setUpvotes(24);
            r2.setViewCount(210);
            r2.setStatus(ReportStatus.VERIFIED);
            communityReportRepository.save(r2);

            CommunityReport r3 = new CommunityReport();
            r3.setReporterName("Karan Mehta");
            r3.setType("URL");
            r3.setTitle("Counterfeit SBI Netbanking Phishing Site");
            r3.setDescription("Fake link received over WhatsApp asking to update PAN card details or account will be suspended within 24 hours. Points to secure-sbi-update-kyc.com.");
            r3.setTargetIdentifier("http://secure-sbi-update-kyc.com/login");
            r3.setTags("Bank Phishing, SBI, PAN Card");
            r3.setCity("Vadodara");
            r3.setState("Gujarat");
            r3.setUpvotes(12);
            r3.setViewCount(98);
            r3.setStatus(ReportStatus.VERIFIED);
            communityReportRepository.save(r3);
        }
    }

    private void seedSettings() {
        if (settingsRepository.count() == 0) {
            settingsRepository.save(new AppSettings("system.version", "2.4.0-ENTERPRISE", "Platform version", "system"));
            settingsRepository.save(new AppSettings("threat.auto_verify_upvotes", "5", "Upvote threshold for auto-verifying community reports", "threat_intel"));
            settingsRepository.save(new AppSettings("analysis.cache_enabled", "true", "Enable memory caching for threat reputation lookups", "performance"));
        }
    }
}
