# GUJARAT TECHNOLOGICAL UNIVERSITY (GTU)
## Course: Advanced Java Programming (Subject Code: BE05000131)
### Semester: V | Academic Year: 2026–2027
---

# PROJECT PRESENTATION DECK
## AUTHENTICA: AI-POWERED MULTI-MODAL SCAM, FRAUD & CYBER THREAT DETECTION PLATFORM
### Problem-Based Learning (PBL) Final Evaluation

---

### SLIDE 1: TITLE SLIDE
- **Project Title:** Authentica - AI-Powered Scam, Fraud & Cyber Threat Detection Platform
- **Course:** Advanced Java Programming (BE05000131)
- **Institution:** Gujarat Technological University (GTU)
- **Technology Stack:** Java 17, Spring Boot 3.2, Spring Security (JWT), Spring Data JPA, Hibernate, H2 / MySQL, Thymeleaf & Vanilla CSS/JS
- **Presentation Type:** Problem-Based Learning (PBL) End Semester Examination (ESE)

---

### SLIDE 2: REAL-WORLD PROBLEM CONTEXT
- **Exponential Rise in Cyber Financial Frauds:** Over 70% of reported cyber crimes in India involve financial fraud and social engineering.
- **Key Threat Vectors:**
  - Deceptive SMS texts threatening electricity power disconnection tonight at 9:30 PM.
  - Phishing web links impersonating SBI, HDFC, or PAN card KYC update portals.
  - Deceptive UPI QR codes claiming "scan to receive cashback/refund."
  - Digital arrest calls impersonating CBI, Mumbai Police, and Customs narcotics departments.
- **Victim Profile:** General citizens, senior citizens, and digitally vulnerable first-time internet users.

---

### SLIDE 3: SHORTCOMINGS OF EXISTING DEFENSES
- **Siloed Solutions:** Standard tools only identify phone numbers, completely ignoring web phishing links and malicious payment QR codes.
- **Reactive Blacklists:** Traditional antivirus blacklists lag 24–48 hours behind newly registered phishing domains and burner SIM cards.
- **Privacy Invasion:** Popular commercial caller ID apps upload citizen address books to third-party proprietary servers.
- **Lack of Explanations:** Existing tools label an alert as "Spam" without explaining *why* or teaching citizens how to identify deceptive traps.

---

### SLIDE 4: PROPOSED SOLUTION: AUTHENTICA
- **Unified Multi-Modal Security Platform:** Inspects URLs, SMS texts, QR codes, phone numbers, and extortion call transcripts in a single interface.
- **Proactive Heuristics in Core Java:** Analyzes psychological urgency triggers, high-abuse TLDs, punycode, and DLT sender violations before links are clicked.
- **UPI Scheme Deconstruction:** Decodes `upi://pay` strings to prevent deceptive debit traps labeled as "refunds."
- **Crowdsourced Intelligence Feed:** Real-time citizen alerts with automated blacklist synchronization.
- **Enterprise Spring Boot Backend:** Portable, high-throughput REST API with an embedded dark-mode web portal.

---

### SLIDE 5: PROJECT OBJECTIVES
1. Re-engineer a complex distributed prototype into a clean, maintainable Spring Boot 3 enterprise application.
2. Build 5 heuristic analysis engines in Core Java delivering sub-50ms scan latency.
3. Implement stateless authentication using HMAC-SHA256 JWT tokens and BCrypt password hashing.
4. Establish seamless dual-database support (zero-configuration embedded H2 and enterprise MySQL).
5. Deliver a responsive, high-aesthetic web interface for interactive examiner testing.

---

### SLIDE 6: SYSTEM ARCHITECTURE & TECH STACK
- **Backend Framework:** Spring Boot 3.2.x (Spring MVC, Spring Data JPA, Spring Security, Spring Validation)
- **Security:** JJWT (HMAC-SHA256), BCrypt Password Encoder, `JwtAuthenticationFilter`
- **Data Access:** Hibernate ORM, Spring Data JPA Repositories
- **Database Engine:** Embedded H2 Database (Default) / MySQL 8.0+ (`--spring.profiles.active=mysql`)
- **Presentation Layer:** Thymeleaf, Semantic HTML5, Custom Vanilla CSS3 (Dark Mode + Glassmorphism), ES6 JavaScript
- **Build System:** Apache Maven 3.9+

---

### SLIDE 7: MULTI-MODAL THREAT INSPECTION MODALITIES
- **1. Web URLs & Domains:** Typosquatting, IP literals, high-abuse TLDs, phishing keyword lures.
- **2. SMS & Message Text:** Urgency indicators, financial lures, fake electricity disconnection warnings.
- **3. QR Codes & UPI Payloads:** VPA handle validation, deceptive transaction notes, merchant ID checks.
- **4. Telephone Numbers:** Fraud registry lookups, report frequency counts, carrier info, DLT compliance.
- **5. Extortion & Voice Transcripts:** Digital arrest patterns, CBI/police impersonation phrases, deepfake speech markers.

---

### SLIDE 8: HEURISTIC ENGINE 1: URL & PHISHING INSPECTOR
- **Blacklist & Whitelist Lookup:** Instant check against national threat databases and government portals (`.gov.in`).
- **Domain Entropy & Structure:** Flags direct IP addresses (e.g., `http://192.168.1.1/login`) and excessive hyphenation.
- **Suspicious TLD Detection:** Identifies `.xyz`, `.top`, `.tk`, `.ml`, `.buzz` domains.
- **Keyword Clustering:** Detects combinations of `kyc`, `sbi`, `secure`, `pan-update`, `login`.
- **Output:** Trust score (0–100), risk tier (`SAFE` to `CRITICAL`), and specific security recommendations.

---

### SLIDE 9: HEURISTIC ENGINE 2: SMS & SMISHING PARSER
- **Psychological Urgency Detection:** Scans for "within 24 hours", "account blocked", "electricity disconnected tonight 9:30 PM".
- **Financial Lure Extraction:** Identifies unrealistic lottery prizes, free cashback, and pre-approved loans.
- **Credential Traps:** Detects APK download links, remote-access app installation requests (AnyDesk, TeamViewer), and OTP requests.
- **TRAI DLT Short-Code Verification:** Flags fake bank sender IDs violating the standard `AB-CD1234` format.

---

### SLIDE 10: HEURISTIC ENGINE 3: UPI & QR SAFETY SHIELD
- **Golden Rule Enforced:** Scanning a QR code **ALWAYS DEBITS** your bank account. You **NEVER** enter a UPI PIN to receive money!
- **Payload Deconstruction:** Parses `upi://pay?pa=...&pn=...&tn=...&am=...`.
- **Deceptive Note Trap Detection:** Flags notes labeled "claim refund" or "receive cashback" designed to steal funds from users who think they are being credited.
- **VPA Fraud Reputation Check:** Matches payee handles against known scam databases.

---

### SLIDE 11: HEURISTIC ENGINE 4: DIGITAL ARREST AUDIO GUARD
- **Modus Operandi Targeted:** Extortionists posing as CBI officers, Mumbai Crime Branch, or Customs claiming an illegal parcel was intercepted at an airport.
- **Keyword & Pattern Matching:** Identifies phrases like "parcel confiscated", "customs narcotics", "digital arrest warrant", "transfer bail amount to RBI account".
- **Immediate Citizen Advisory:** Warns that Indian law enforcement **NEVER** conducts court proceedings or arrests over Skype or WhatsApp video calls.

---

### SLIDE 12: CROWDSOURCED THREAT INTELLIGENCE & AUTO-SYNC
- **Decentralized Citizen Reporting:** Users submit scam SMS, calls, links, and UPI IDs with location tags (City, State) and narrative descriptions.
- **Democratic Community Validation:** Members upvote genuine fraud warnings or downvote disputed reports.
- **Automated Intelligence Promotion:** When a report reaches the configured upvote threshold (default: 5), it is automatically promoted to the global fraud blacklist (`fraud_numbers` or `fraud_urls`).

---

### SLIDE 13: CITIZEN CYBER HYGIENE TRUST INDEX
- **Personalized Risk Index:** Each registered citizen has a dynamic Trust Score (0 to 100) and Risk Tier (`SAFE`, `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`).
- **Continuous Audit:** Every scan updates the user's score based on the frequency and severity of threats they encounter.
- **Audit History Trail:** Paginated history log allows citizens to review past scans and soft-delete/archive records.

---

### SLIDE 14: SECURITY ARCHITECTURE (JWT & BCRYPT)
- **Stateless RESTful Security:** Eliminates server-side session overhead using RFC 7519 compliant JSON Web Tokens.
- **HMAC-SHA256 Signing:** 256-bit secret key ensures tokens cannot be forged or tampered with.
- **Custom Filter Chain:** `JwtAuthenticationFilter` validates tokens and establishes `SecurityContext` on every private request.
- **BCrypt Password Hashing:** Automatically salts and hashes credentials with adaptive computational cost factor 10.
- **Role-Based Access Control:** Strict partitioning between `ROLE_USER` and `ROLE_ADMIN`.

---

### SLIDE 15: DUAL DATABASE PERSISTENCE ARCHITECTURE
- **Seamless Portability:** Spring Data JPA entity annotations remain identical across database engines.
- **Default Embedded H2 Database:** Zero-friction evaluation; running `mvn spring-boot:run` works out of the box with auto-schema creation and rich seed data.
- **Enterprise MySQL Compatibility:** Run with `--spring.profiles.active=mysql` for production environments.
- **Embedded Web Console:** Examiners can inspect database tables at `http://localhost:8080/h2-console`.

---

### SLIDE 16: WEB APPLICATION UI WALKTHROUGH
- **Home / Landing Portal (`/`):** Hero section, platform metrics, and instant scan widget.
- **Scanner Studio (`/scanner`):** Dedicated tabs for URL, SMS, QR, Phone, and Voice with 1-click test attack buttons.
- **Threat Intel Feed (`/community`):** Live crowdsourced threat cards, upvoting, and report submission modal.
- **Citizen Dashboard (`/dashboard`):** Personal trust index meter and paginated scan logs.
- **Admin Moderation Console (`/admin`):** System telemetry cards, report approval/rejection queue, and setting editor.
- **Documentation & Viva Guide (`/docs`):** Complete REST API catalog and Viva Voce examination Q&A.

---

### SLIDE 17: TESTING & QUALITY ASSURANCE
- **Unit Testing:** JUnit 5 and Mockito tests in `src/test/java/com/authentica/` verifying heuristic scoring and whitelist overrides.
- **Integration Testing:** Context initialization tests validating Spring bean wiring.
- **Zero-Crash Resilience:** Exception-handling patterns ensure invalid or malformed scan inputs return structured error responses instead of unhandled HTTP 500 crashes.
- **Performance:** Heuristic scan operations execute with average latency between 2ms and 25ms.

---

### SLIDE 18: SOCIETAL & CYBERSECURITY IMPACT
- **Empowering Ordinary Citizens:** Translates complex cyber threat data into simple, actionable safety steps.
- **Preventing Financial Loss:** Catches phishing and UPI scams *before* credentials or money are transferred.
- **Citizen Data Privacy:** Zero address book exfiltration; all scans are opt-in and private.
- **Collaborative Cyber Defense:** Transforms passive victims into active community guardians.

---

### SLIDE 19: FUTURE ENHANCEMENTS
- **Native Telecom Webhooks:** Automated scanning of incoming SMS messages via carrier APIs.
- **On-Device Edge ML:** Lightweight TensorFlow Lite models running on mobile devices for offline audio transcription.
- **NCRP 1930 Integration:** 1-click citizen cybercrime FIR generation forwarded directly to the Indian National Cyber Crime Reporting Portal.
- **Regional Languages Support:** Multi-lingual smishing detection for Hindi, Gujarati, Tamil, Telugu, and other Indian regional languages.

---

### SLIDE 20: CONCLUSION & VIVA VOCE Q&A
- **Summary:** Authentica successfully bridges theoretical Advanced Java concepts (Spring Boot, Hibernate, Sockets, Security, MVC) with a pressing real-world cyber defense challenge.
- **Readiness:** Complete source code in `ajava/`, comprehensive documentation across all 7 PBL modules, pre-seeded demo records, and zero-configuration launch.
- **Thank You!** Open for questions and live system demonstration.
