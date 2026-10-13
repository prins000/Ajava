package com.authentica.service;

import com.authentica.model.*;
import com.authentica.model.enums.*;
import com.authentica.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataInitializerService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FraudNumberRepository fraudNumberRepository;
    private final FraudURLRepository fraudURLRepository;
    private final FraudQRRepository fraudQRRepository;
    private final CommunityReportRepository communityReportRepository;
    private final WhitelistRepository whitelistRepository;
    private final AppSettingsRepository settingsRepository;
    private final FeedbackRepository feedbackRepository;
    private final ProtectedContactRepository contactRepository;
    private final SupportTicketRepository ticketRepository;
    private final HeuristicRuleRepository heuristicRuleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializerService(UserRepository userRepository,
                                  FraudNumberRepository fraudNumberRepository,
                                  FraudURLRepository fraudURLRepository,
                                  FraudQRRepository fraudQRRepository,
                                  CommunityReportRepository communityReportRepository,
                                  WhitelistRepository whitelistRepository,
                                  AppSettingsRepository settingsRepository,
                                  FeedbackRepository feedbackRepository,
                                  ProtectedContactRepository contactRepository,
                                  SupportTicketRepository ticketRepository,
                                  HeuristicRuleRepository heuristicRuleRepository,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.fraudNumberRepository = fraudNumberRepository;
        this.fraudURLRepository = fraudURLRepository;
        this.fraudQRRepository = fraudQRRepository;
        this.communityReportRepository = communityReportRepository;
        this.whitelistRepository = whitelistRepository;
        this.settingsRepository = settingsRepository;
        this.feedbackRepository = feedbackRepository;
        this.contactRepository = contactRepository;
        this.ticketRepository = ticketRepository;
        this.heuristicRuleRepository = heuristicRuleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedWhitelists();
        seedFraudIntelligence();
        seedCommunityReports();
        seedSettings();
        seedSupportAndFeedback();
        seedDynamicRules();
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

    private void seedSupportAndFeedback() {
        if (ticketRepository.count() == 0) {
            SupportTicket t1 = new SupportTicket(
                    null,
                    "aarav.sharma@example.com",
                    "Suspicious Collect Request on Google Pay",
                    "Received a collect request of Rs 4,999 under note 'claim cashback'. The QR scanner flagged it as deceptive.",
                    TicketPriority.HIGH
            );
            ticketRepository.save(t1);
        }

        if (feedbackRepository.count() == 0) {
            Feedback f1 = new Feedback(
                    null,
                    "user@authentica.io",
                    "detection",
                    5,
                    "Urgency heuristic engine immediately flagged the fake electricity disconnection SMS. Very helpful!"
            );
            feedbackRepository.save(f1);
        }

        userRepository.findByEmail("user@authentica.io").ifPresent(user -> {
            if (contactRepository.findByUserId(user.getId()).isEmpty()) {
                contactRepository.save(new ProtectedContact(user.getId(), "Ramesh Sharma (Father)", "+919876500001", "Father"));
                contactRepository.save(new ProtectedContact(user.getId(), "Sunita Sharma (Mother)", "+919876500002", "Mother"));
            }
        });
    }

    private void seedDynamicRules() {
        if (heuristicRuleRepository.count() == 0) {
            // URL Keywords
            String[] urlKeywords = {
                    "kyc", "sbi", "hdfc", "icici", "pnb", "paytm", "phonepe", "bank", "secure", "login",
                    "verify", "blocked", "update-pan", "refund", "lottery", "gift", "electricity-bill", "bonus",
                    "netflix", "crypto", "binance", "amazon-gift", "part-time-job", "task-earn", "telegram-vip",
                    "disconnection", "unblock", "aadhaar", "free-recharge"
            };
            for (String kw : urlKeywords) {
                heuristicRuleRepository.save(new HeuristicRule(RuleCategory.URL_KEYWORD, kw, 15, "Security lure keyword detected: '" + kw + "'"));
            }

            // Suspicious TLDs
            String[] tlds = {
                    ".tk", ".ml", ".ga", ".cf", ".gq", ".xyz", ".top", ".buzz", ".work", ".click", ".fit", ".casa", ".monster", ".icu"
            };
            for (String tld : tlds) {
                heuristicRuleRepository.save(new HeuristicRule(RuleCategory.SUSPICIOUS_TLD, tld, 25, "High-abuse top-level domain extension: " + tld));
            }

            // SMS Urgency Triggers
            String[] urgency = {
                    "immediate action", "within 24 hours", "account blocked", "card suspended",
                    "electricity will be disconnected", "tonight 9:30 pm", "urgent notice", "last warning",
                    "deactivated immediately", "legal action", "arrest warrant", "final notice", "cut supply",
                    "sim blocked", "services terminated"
            };
            for (String u : urgency) {
                heuristicRuleRepository.save(new HeuristicRule(RuleCategory.SMS_URGENCY, u, 25, "Psychological urgency pressure tactic: '" + u + "'"));
            }

            // SMS Financial Lures
            String[] lures = {
                    "won lottery", "cashback of rs", "congratulations you won", "reward points expired",
                    "pre-approved loan", "claim bonus", "free gift", "transfer rs", "credited to your account",
                    "work from home daily 5000", "review hotels earn", "youtube like and earn", "task bonus"
            };
            for (String l : lures) {
                heuristicRuleRepository.save(new HeuristicRule(RuleCategory.SMS_FINANCIAL_LURE, l, 25, "Deceptive financial reward lure: '" + l + "'"));
            }

            // SMS Credential Traps
            String[] traps = {
                    "click link to update kyc", "pan card update", "share otp", "enter your pin",
                    "download apk", "install anydesk", "install teamviewer", "verify aadhar",
                    "update bank details", "send debit card details", "install quicksupport", "update apk", "install rustdesk"
            };
            for (String tr : traps) {
                heuristicRuleRepository.save(new HeuristicRule(RuleCategory.SMS_CREDENTIAL_TRAP, tr, 35, "Malicious remote access or credential harvest trap: '" + tr + "'"));
            }

            // Voice Extortion / Digital Arrest
            String[] voice = {
                    "police department", "customs narcotics", "digital arrest", "parcel confiscated",
                    "illegal passport found", "cbi officer", "crime branch", "arrest warrant",
                    "remain on video call", "do not disconnect", "transfer bail amount to rbi verification account",
                    "enforcement directorate", "trai telecom disconnection", "anti money laundering cell",
                    "supreme court warrant", "narcotics control bureau"
            };
            for (String v : voice) {
                heuristicRuleRepository.save(new HeuristicRule(RuleCategory.VOICE_EXTORTION, v, 35, "Coercive digital arrest / state officer impersonation marker: '" + v + "'"));
            }

            // QR / UPI Deceptive Markers
            String[] qr = {
                    "refund", "cashback", "customer care", "support desk", "rbi verification",
                    "electricity bill", "bonus cash", "lottery claim", "helpdesk", "account unlock"
            };
            for (String q : qr) {
                heuristicRuleRepository.save(new HeuristicRule(RuleCategory.QR_DECEPTIVE, q, 35, "Deceptive merchant name masking reverse collect request: '" + q + "'"));
            }
        }
    }
}
