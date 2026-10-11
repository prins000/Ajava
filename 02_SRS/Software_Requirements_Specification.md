# Gujarat Technological University (GTU)
## Course: Advanced Java Programming (Subject Code: BE05000131)
### Semester: V | Academic Year: 2026-2027
---

# SOFTWARE REQUIREMENTS SPECIFICATION (SRS)
## Project Name: Authentica Scam & Cyber Threat Detection Platform

---

## 1. Introduction

### 1.1 Purpose of the System
The purpose of this Software Requirements Specification (SRS) document is to define the complete functional and non-functional requirements for the **Authentica Scam & Cyber Threat Detection Platform**. This document serves as the formal specification for engineering, implementing, validating, and evaluating the platform under the Advanced Java Programming (BE05000131) Problem-Based Learning curriculum.

### 1.2 Problem Overview
Social engineering, phishing, smishing, UPI payment deception, and digital extortion attacks in India have reached alarming proportions. Existing consumer security solutions are either siloed (only identifying phone callers), static (dependent on stale blacklists), or closed-source and invasive of user privacy. Authentica provides a unified, heuristic-driven, multi-modal detection engine built in Java that analyzes web links, SMS messages, phone numbers, UPI QR codes, and extortion transcripts in real time, accompanied by crowdsourced intelligence and dynamic user trust scoring.

### 1.3 Scope of the System
- **Backend Architecture:** A modular Spring Boot 3 enterprise application utilizing Spring MVC, Spring Data JPA, and Spring Security.
- **Threat Engines:** Java-based heuristic analyzers evaluating URLs, texts, QR codes, phone numbers, and call transcripts.
- **Identity & Access Management:** Stateless JWT authentication, role-based authorization (`ROLE_USER` and `ROLE_ADMIN`), and BCrypt password encryption.
- **Persistence:** Hibernate ORM with dual-engine capability: embedded H2 for portable zero-configuration academic evaluation, and MySQL for production deployments.
- **User Interface:** A responsive, cyber-security themed web portal (HTML5, CSS3, JS, Thymeleaf) providing scanner studio, crowdsourced threat board, citizen dashboard, and administrator moderation console.

### 1.4 Intended Users
1. **Citizens / End Users:** Individuals checking suspicious messages, links, callers, or payment QRs.
2. **Community Contributors:** Users submitting fraud incident reports and upvoting/downvoting community alerts.
3. **System Administrators:** Security officers managing user accounts, moderating reports, and configuring threat thresholds.
4. **Academic Evaluators & Examiners:** Faculty reviewing architectural compliance, design patterns, and code quality.

### 1.5 Definitions, Acronyms, and Abbreviations
- **API:** Application Programming Interface
- **BCrypt:** Adaptive cryptographic hash function for passwords
- **DLT:** Distributed Ledger Technology (TRAI telemarketing regulatory framework)
- **GTU:** Gujarat Technological University
- **H2:** Embedded in-memory / disk relational database engine written in Java
- **JPA:** Java Persistence API
- **JWT:** JSON Web Token (RFC 7519)
- **NCRP:** National Cyber Crime Reporting Portal
- **ORM:** Object-Relational Mapping
- **PBL:** Problem-Based Learning
- **REST:** Representational State Transfer
- **SRS:** Software Requirements Specification
- **TLD:** Top-Level Domain
- **UPI:** Unified Payments Interface
- **VPA:** Virtual Payment Address (e.g., `user@upi`)

---

## 2. Overall Description

### 2.1 Product Perspective
Authentica is an autonomous, full-stack, enterprise-grade software application. It operates as a centralized threat analysis server that exposes RESTful APIs to client applications (web portals, mobile apps, third-party microservices) while providing an integrated, high-aesthetic web user interface.

```
+-----------------------------------------------------------------+
|                       Client Tier                               |
|   Web Browser (Dashboard/Studio)    Mobile Apps (React Native)  |
+-----------------------------------------------------------------+
                                | HTTP/REST (JSON + JWT)
                                v
+-----------------------------------------------------------------+
|               Spring Boot Application Tier (ajava)              |
|  Controllers (Auth, Analysis, Community, Admin, WebViewController) |
|  Security Filter (JWT Filter, BCrypt, SecurityConfig)          |
|  Services (AuthService, AnalysisService, CommunityReportService) |
|  Engines (UrlEngine, TextEngine, QrEngine, VoiceEngine, Number) |
+-----------------------------------------------------------------+
                                | Spring Data JPA / Hibernate
                                v
+-----------------------------------------------------------------+
|                      Persistence Tier                           |
|       H2 (In-Memory/File Default)  <--->  MySQL (Production)    |
+-----------------------------------------------------------------+
```

### 2.2 Product Features
- Multi-modal threat scanner (URL, SMS, QR, Phone, Voice).
- Heuristic threat scoring (0 to 100) and risk classification (SAFE, LOW, MEDIUM, HIGH, CRITICAL).
- Actionable security recommendations for users.
- Crowdsourced community threat reporting and upvoting.
- Automatic intelligence synchronization from community reports to blacklists.
- Citizen Cyber Hygiene Trust Index.
- Administrative dashboard with real-time telemetry.

### 2.3 User Classes and Characteristics
- **Guest / Unregistered User:** Can run public threat scans and read the public threat feed.
- **Registered User (ROLE_USER):** Can run scans with history recording, track personal risk score, submit community reports, and vote.
- **Administrator (ROLE_ADMIN):** Has full access to moderation queue, user management, and system configuration.

### 2.4 Operating Environment
- **Runtime Environment:** Java Runtime Environment (JRE) 17 or higher.
- **Operating System:** Platform independent (Windows 10/11, Ubuntu/Linux, macOS).
- **Web Browser:** Any modern browser supporting HTML5 and CSS3 (Chrome, Edge, Firefox, Safari).
- **Database:** Embedded H2 Database (zero external setup) or MySQL 8.0+.
- **Build & Dependency Management:** Apache Maven 3.8+.

### 2.5 Assumptions and Dependencies
- Standard internet connection for external domain resolution when running in production mode.
- Users access the system via standard HTTP/HTTPS protocols on default port 8080.
- Database automatically initializes default admin and demo records on first startup.

### 2.6 Constraints
- Password storage must comply with cryptographic salt-and-hash standards (BCrypt).
- Scans must execute synchronously in under 150ms without blocking request threads.
- Standalone execution must require zero external database setup for evaluation convenience.

---

## 3. Functional Requirements

### 3.1 Authentication & User Management
- **FR-01 (User Registration):** The system shall allow users to register using name, email, mobile number, and password. Duplicate email registration shall be rejected with HTTP 400.
- **FR-02 (User Login):** The system shall authenticate users using email and password, issuing a signed HMAC-SHA256 JWT access token upon successful credentials verification.
- **FR-03 (Token Validation):** The system shall intercept every private request through `JwtAuthenticationFilter` and validate the signature and expiration of the bearer token.
- **FR-04 (Profile Retrieval):** The system shall provide an endpoint `/api/v1/auth/me` returning the current user's profile, role, trust score, and risk level.

### 3.2 Threat Analysis & Heuristic Engines
- **FR-05 (Public Threat Scan):** The system shall provide a public scan endpoint `/api/v1/analysis/scan-public` allowing immediate inspection of URLs, SMS texts, QR codes, phone numbers, and audio transcripts without mandatory authentication.
- **FR-06 (Authenticated Threat Scan):** The system shall allow registered users to run scans via `/api/v1/analysis/scan` and automatically record the analysis in their audit history.
- **FR-07 (URL Threat Inspection):** The system shall analyze URLs for known blacklists, whitelists, suspicious TLDs (`.xyz`, `.top`, etc.), direct IP hostnames, and phishing keywords (`kyc`, `sbi`, `pan-update`, etc.).
- **FR-08 (SMS Smishing Inspection):** The system shall analyze message text for psychological urgency phrases ("within 24 hours", "disconnected tonight"), financial lures, credential harvest traps, and embedded URLs.
- **FR-09 (QR & UPI Scheme Verification):** The system shall deconstruct UPI URI payloads (`upi://pay`), validate payee VPA against known fraud handles, and flag deceptive "claim refund" transaction notes.
- **FR-10 (Voice & Digital Arrest Analysis):** The system shall evaluate speech transcripts for law enforcement impersonation keywords ("CBI", "customs narcotics", "digital arrest warrant").
- **FR-11 (Phone Number Reputation Lookup):** The system shall query phone numbers against verified fraud databases, reporting carrier, spam tags, and previous community report counts.

### 3.3 History & Audit Trail
- **FR-12 (History Retrieval):** The system shall return a paginated list of all threat scans performed by the authenticated user.
- **FR-13 (Scan Archival):** The system shall allow users to soft-delete/archive individual scan entries from their audit trail.
- **FR-14 (Trust Score Recalculation):** The system shall dynamically adjust the user's trust score and risk level based on recent scan results and exposure history.

### 3.4 Crowdsourced Community Threat Intelligence
- **FR-15 (Submit Incident Report):** The system shall allow users to submit fraud incident reports with category, title, description, target identifier, location, and tags.
- **FR-16 (Public Threat Feed):** The system shall provide a public endpoint `/api/v1/community/feed` displaying verified threat reports ordered by timestamp or popularity.
- **FR-17 (Upvote / Downvote):** The system shall allow users to upvote or downvote community reports.
- **FR-18 (Automatic Blacklist Sync):** When a report accumulates upvotes exceeding the configured threshold (default: 5), the system shall automatically promote the target identifier into the active fraud database (`FraudNumber` or `FraudURL`).

### 3.5 Administrative Moderation & Configuration
- **FR-19 (Analytics Telemetry):** The system shall provide administrators with platform metrics (total users, active users, total scans, community reports count, open support tickets).
- **FR-20 (Incident Moderation):** The system shall allow administrators to approve (`VERIFIED`) or reject (`REJECTED`) pending community incident reports.
- **FR-21 (System Settings Management):** The system shall permit administrators to update operational parameters (upvote threshold, cache flags) via `/api/v1/admin/settings`.

---

## 4. Non-Functional Requirements

### 4.1 Performance (NFR-01 to NFR-03)
- **NFR-01 (Response Time):** Heuristic analysis requests must return a complete response within 100 milliseconds under standard load.
- **NFR-02 (Throughput):** The application shall support at least 150 concurrent scan requests per second on standard multi-core hardware.
- **NFR-03 (Resource Footprint):** Memory consumption of the running embedded application must remain under 350 MB RAM in idle state.

### 4.2 Security (NFR-04 to NFR-07)
- **NFR-04 (Password Cryptography):** Passwords must never be stored in plain text; all credentials must be hashed using BCrypt with minimum work factor 10.
- **NFR-05 (Stateless Authentication):** Sensitive endpoints must mandate valid HMAC-SHA256 signed JWT tokens in the `Authorization: Bearer <token>` header.
- **NFR-06 (Protection against Injections):** All database interactions must use parameterized queries through JPA and Spring Data repositories to prevent SQL injection.
- **NFR-07 (Data Sanitization):** User-supplied content displayed in the web dashboard must be HTML-escaped to prevent Cross-Site Scripting (XSS).

### 4.3 Reliability & Availability (NFR-08 to NFR-09)
- **NFR-08 (Zero-Crash Fallback):** Malformed URLs, anomalous phone numbers, or invalid QR inputs must be handled gracefully with descriptive error payloads without throwing unhandled HTTP 500 exceptions.
- **NFR-09 (Availability):** The application service is designed for 99.9% uptime with stateless session management.

### 4.4 Portability & Usability (NFR-10 to NFR-12)
- **NFR-10 (Portability):** The system must execute identically across Windows, Linux, and macOS without requiring operating system-specific configuration.
- **NFR-11 (Zero External Dependency):** By default, the application must run self-contained with embedded H2 database without requiring pre-installed MySQL or MongoDB servers.
- **NFR-12 (Usability & Aesthetics):** The web user interface must feature a modern cyber-security dark theme, responsive layout, clear metric visualizations, and 1-click sample attacks for academic demonstration.
