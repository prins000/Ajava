# Gujarat Technological University (GTU)
## Course: Advanced Java Programming (Subject Code: BE05000131)
### Semester: V | Academic Year: 2026-2027
---

# PROBLEM-BASED LEARNING (PBL) PROJECT PROPOSAL

## 1. Project Title
**Authentica: AI-Powered Multi-Modal Scam, Fraud, and Cyber Threat Detection Platform**

---

## 2. Problem Statement
### 2.1 What is the problem?
Digital financial transactions, unified payments (UPI), mobile banking, and digital messaging channels in India and worldwide have grown exponentially. However, this growth has coincided with a surge in sophisticated social engineering attacks, digital financial frauds, and cyber extortions. Key threat vectors include:
- **Phishing & Typosquatting URLs:** Spoofed netbanking portals harvest credentials, PAN card numbers, and debit card details.
- **Smishing (SMS Phishing):** Deceptive messages claiming immediate disconnection of electricity power or suspension of bank accounts within 24 hours create psychological panic.
- **UPI QR Code Payment Fraud:** Malicious QR codes deceptively labeled as "cashback" or "claim refund" trick unsuspecting victims into authorizing debits with their UPI PIN.
- **Impersonation & Digital Arrest Extortion:** Scammers pose as police officials, CBI officers, or customs agents over VoIP and phone calls, coercing victims into transferring funds to "safe verification accounts."
- **Spam & Fraud Telephony:** Unregistered telemarketers and fraud syndicates spoofing numbers and bypassing telecom DLT guidelines.

### 2.2 Difficulties / Issues with the Existing Situation
- **Fragmented Threat Defenses:** Existing anti-spam applications focus solely on caller identification or basic SMS filtering, lacking unified multi-modal cross-correlation across URLs, QR codes, SMS, and extortion audio.
- **Reactive Blacklists Only:** Traditional tools rely on static, historical blacklists. Brand new phishing domains and newly activated burner SIM cards evade detection during their first 24–48 hours.
- **Lack of Transparent Explanations:** Existing tools simply flag items as "Spam" without explaining *why* (e.g., urgency triggers, DLT violation, deceptive UPI debit note), preventing users from developing cyber hygiene.
- **Absence of Unified Enterprise APIs:** Small businesses, cooperative banks, and mobile apps lack access to an easily embeddable, standards-based Java REST backend that can audit threats in real time.

---

## 3. Target Users / Stakeholders
1. **General Citizens & Mobile Users:** Individuals vulnerable to smishing, fake lottery messages, utility bill disconnect scams, and deceptive payment QR codes.
2. **Senior Citizens & Digitally Vulnerable Groups:** Populations frequently targeted by high-pressure "digital arrest" and bank KYC panic calls.
3. **Cybersecurity Community & Law Enforcement:** Citizen reporting groups, telecom regulators (TRAI), and cyber cells (1930 / cybercrime.gov.in) who need crowd-sourced threat intelligence.
4. **Enterprises & Financial Institutions:** Cooperative banks, fintechs, and payment gateways seeking heuristic threat verification endpoints.
5. **System Administrators & Security Analysts:** Enterprise teams monitoring fraud trends and managing blacklists and whitelists.

---

## 4. Need for the Proposed Solution
### 4.1 Why should this problem be solved?
Financial fraud causes severe emotional trauma and irreversible financial losses, especially to middle-class citizens and seniors. According to national cybercrime reports, financial fraud accounts for over 70% of reported cyber incidents. Rapid detection and proactive warnings at the moment of interaction (before clicking a link or scanning a QR code) can prevent fraud before funds are transferred.

### 4.2 Limitations of Existing Systems / Processes
- Static rule matching fails against dynamically generated phishing URLs.
- Manual verification through official customer care lines takes hours, whereas scams demand action within minutes.
- Commercial caller ID apps compromise user privacy by uploading entire address books to external proprietary servers.

---

## 5. Proposed Solution
### 5.1 What solution are you proposing?
**Authentica** is an enterprise-grade Java web application and threat intelligence platform designed to provide proactive, multi-modal protection. Built on Spring Boot 3, Spring Data JPA, and Spring Security, Authentica introduces:
1. **Multi-Modal Threat Engine:** Real-time heuristic analyzers for Web URLs, SMS texts, QR codes/UPI payloads, Phone numbers, and Extortion voice transcripts.
2. **Heuristic & Linguistic Analysis:** Detects psychological panic language, urgency triggers, DLT sender format violations, and spoofed netbanking domains even if they are not yet present in static blacklists.
3. **UPI Payment Intent Verification:** Automatically deconstructs `upi://pay` strings to confirm that QR scanning only debits money and identifies deceptive refund notes.
4. **Community Threat Intelligence:** A crowdsourced threat feed where users report suspicious numbers/links, vote on validity, and automatically synchronize verified threats into the blacklist.
5. **Citizen Cyber Hygiene Dashboard:** Provides individual trust scores (0–100) and an immutable audit log of scans.
6. **Zero-Trust Administrative Telemetry:** Allows administrators to moderate crowdsourced reports, configure risk thresholds, and analyze fraud trends.

---

## 6. Objectives of the Project
1. Design and develop an enterprise-ready Spring Boot backend providing robust RESTful APIs for threat analysis.
2. Implement heuristic inspection engines in Core Java for URL, SMS text, QR/UPI, phone number, and voice transcript analysis.
3. Implement Spring Security with JSON Web Tokens (JWT) and BCrypt encryption for role-based access control (`ROLE_USER`, `ROLE_ADMIN`).
4. Utilize Spring Data JPA and Hibernate for database persistence supporting both H2 (in-memory/embedded) and MySQL.
5. Build an intuitive, high-aesthetic web dashboard (dark theme, glassmorphism) for immediate inspection, incident reporting, and administrative moderation.
6. Create comprehensive documentation adhering to GTU PBL requirements (SRS, UML diagrams, SQL scripts, Project Report, Presentation).

---

## 7. Scope of the Project
### In Scope:
- Multi-modal scan engines for URLs, SMS messages, phone numbers, UPI QR codes, and call transcripts.
- User registration, authentication, JWT token issuance, and profile management.
- Dynamic Trust Score calculation and personal threat history auditing.
- Community incident reporting, voting mechanism, and automatic blacklist promotion.
- Administrative moderation console with system metrics and setting controls.
- Whitelist verification for official government (`.gov.in`) and banking domains.
- Dual database support (zero-configuration embedded H2 and production MySQL).

### Out of Scope:
- Live telecom carrier network tapping (simulated via API and standard formats).
- Native iOS/Android binary build compilation (provided via responsive web portal and REST APIs compatible with React Native/Android clients).

---

## 8. Major Features of the Proposed System
- **F-01: Instant Scam Scanner Studio:** 1-click inspection for URLs, SMS text, QR strings, and telephone numbers.
- **F-02: Heuristic Urgency Detector:** Natural language pattern matching for artificial urgency, financial lures, and credential theft traps.
- **F-03: UPI Scheme Validator:** Deep extraction of Payee VPA, payee name, transaction note, and amount.
- **F-04: Digital Arrest Extortion Flagging:** Identifies fraudulent law enforcement impersonation patterns.
- **F-05: Crowdsourced Threat Feed:** Real-time feed of local fraud incidents with upvoting and location tags.
- **F-06: Automated Threat Intelligence Sync:** Reports reaching verified upvote threshold automatically update the global blacklist.
- **F-07: User Trust & Exposure Index:** Dynamic scoring algorithm assessing user exposure to cyber risks.
- **F-08: Admin Moderation Suite:** Comprehensive management of users, tickets, reports, and threat rules.

---

## 9. Proposed Technologies, Tools and Frameworks
- **Programming Language:** Java 17 / Java 21 (Core Java, OOP, Collections, Regex, Concurrency)
- **Framework:** Spring Boot 3.2.x (Spring MVC, Spring Data JPA, Spring Security, Spring Validation)
- **ORM & Database:** Hibernate ORM, H2 Database Engine, MySQL Connector/J
- **Security & Tokens:** Spring Security 6, JJWT (HMAC-SHA256), BCrypt Password Encoder
- **Frontend / Presentation:** Thymeleaf, HTML5, Vanilla CSS3 (Custom Dark Mode Cyber Theme), JavaScript ES6 (Fetch API, DOM manipulation)
- **Build Tool:** Apache Maven 3.9+
- **API Testing:** Postman / cURL / Built-in Interactive Web Console
- **IDE:** Eclipse / IntelliJ IDEA / VS Code

---

## 10. Methodology / Development Approach
The project follows an **Iterative Agile & Component-Based Engineering Approach**:
1. **Domain Modeling & Architectural Design:** Designing database entities, enums, and JPA repositories.
2. **Security & Authentication Layer:** Implementing JWT filter chain and user access policies.
3. **Engine Development:** Building specialized heuristic engines using Java pattern matching and data structures.
4. **Service & Controller Integration:** Exposing RESTful endpoints and linking business logic.
5. **View & Presentation Engineering:** Crafting responsive user interfaces and interactive dashboards.
6. **Testing & Validation:** Unit testing detection engines and conducting integration testing across H2 and MySQL.

---

## 11. Expected Outcome
A fully operational, standalone, enterprise-grade Java web application capable of running with a single Maven command (`mvn spring-boot:run`), complete with pre-seeded demo scam data, a modern dark-mode user interface, RESTful APIs, and full GTU PBL academic documentation.

---

## 12. Future Scope
- Integration with live telecom APIs for automated real-time SMS webhook analysis.
- Deployment of on-device lightweight TensorFlow Lite models for client-side offline audio transcription.
- Federated learning across regional nodes for zero-knowledge privacy-preserving threat intelligence.
- Direct integration with Indian National Cyber Crime Reporting Portal (NCRP) API for one-click citizen FIR filing.

---

## 13. References
1. Gujarat Technological University, "Advanced Java Programming Syllabus & PBL Guidelines," Subject Code: BE05000131.
2. Indian Cyber Crime Coordination Centre (I4C), Ministry of Home Affairs, "Citizen Cyber Safety Advisory Reports (2024-2025)."
3. Spring Boot Official Documentation, https://docs.spring.io/spring-boot/docs/current/reference/html/
4. Craig Walls, *Spring in Action, 6th Edition*, Manning Publications.
5. IEEE Std 830-1998, "IEEE Recommended Practice for Software Requirements Specifications."
