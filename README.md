# Authentica: AI-Powered Scam, Fraud & Cyber Threat Detection Platform
### Gujarat Technological University (GTU)
**Course:** Advanced Java Programming (Subject Code: **BE05000131**)  
**Semester:** V (Computer Engineering / Information Technology)  
**Curriculum Activity:** Problem-Based Learning (PBL) Submission Package  

---

## 📌 Executive Summary
**Authentica** is an enterprise-grade cyber defense and scam detection platform re-engineered in **Spring Boot 3**, **Spring Security 6**, and **Spring Data JPA / Hibernate**. It protects citizens against phishing links, urgency-driven SMS scams, deceptive UPI QR code debits, and digital arrest extortion calls through real-time heuristic detection engines, crowdsourced community intelligence, and citizen cyber hygiene profiling.

---

## 📁 GTU PBL Submission Package Structure

Following the authoritative guidelines in `PBL requirements.pdf`, this submission is organized into the required 7 submission modules plus the runnable source codebase:

```
├── PLAN.md                               <- Strategic 1-page milestone development plan
├── ASSUMPTIONS.md                        <- Technical assumptions & architectural decisions
├── README.md                             <- Master submission overview & quickstart guide
│
├── 01_Project_Proposal/
│   └── Project_Proposal.md               <- Project proposal meeting all 13 GTU checklist sections
│
├── 02_SRS/
│   └── Software_Requirements_Specification.md <- IEEE-830 aligned Software Requirements Specification
│
├── 03_UML/
│   └── UML_Diagrams.md                   <- All 7 UML diagrams (Use Case, Class, Sequence, Activity,
│                                            State Machine, Component, Deployment) in Mermaid & PlantUML
│
├── 04_Source_Code/
│   └── README.md                         <- Source code architecture manifest & file guide
│
├── 05_Database/
│   ├── schema.sql                        <- Complete DDL schema script (MySQL / H2 / PostgreSQL)
│   ├── data.sql                          <- Rich DML seed script (users, admin, verified blacklists)
│   └── Database_Documentation.md         <- ER diagrams and complete data dictionary
│
├── 06_Project_Report/
│   └── Final_Project_Report.md           <- Comprehensive GTU academic project report (8 chapters)
│
├── 07_Presentation/
│   └── Presentation_Deck.md              <- 20-slide viva voce presentation deck
│
└── ajava/                                <- Complete, runnable Spring Boot 3 enterprise application
    ├── pom.xml                           <- Maven build descriptor
    └── src/
        ├── main/
        │   ├── java/com/authentica/      <- Models, Repositories, Services, Engines, Controllers, Security
        │   └── resources/
        │       ├── application.properties        <- Default embedded H2 & JWT configuration
        │       ├── application-mysql.properties  <- Production MySQL profile configuration
        │       ├── static/                       <- Cyber theme CSS & client JavaScript
        │       └── templates/                    <- HTML views (Dashboard, Scanner, Community, Admin, Docs)
        └── test/
            └── java/com/authentica/      <- JUnit 5 & Mockito test suite
```

---

## 🚀 Quickstart: How to Run the Application

The application is completely self-contained with an embedded H2 database engine. It requires zero database configuration on the evaluator's computer.

### Step 1: Open Terminal & Navigate to `ajava`
```bash
cd ajava
```

### Step 2: Launch the Spring Boot Server
```bash
mvn clean spring-boot:run
```
*(Alternatively, build an executable JAR using `mvn clean package -DskipTests` and run with `java -jar target/authentica-platform-1.0.0.jar`).*

### Step 3: Open in Web Browser
Open your browser and navigate to:
👉 **`http://localhost:8080/`**

---

## 🔑 Default Seeded Credentials

| User Role | Email Address | Password | Permissions |
|---|---|---|---|
| **System Administrator** | `admin@authentica.io` | `Admin@1234` | Full access to Moderation, Telemetry, and Configuration |
| **Citizen Demo User** | `user@authentica.io` | `User@1234` | Scanner Studio, History, Trust Score, Community Reports |
| **Embedded Database Console** | `http://localhost:8080/h2-console` | User: `sa` / Pass: *(empty)* | JDBC URL: `jdbc:h2:mem:authenticadb` |

---

## 🌐 Web Application Features & Navigation

- **Landing & Instant Analyzer (`/`):** Hero overview, system metrics, and quick threat scanner.
- **Scanner Studio (`/scanner`):** Dedicated multi-modal analysis suite with 1-click demo buttons for:
  - 🌐 **Web URLs:** Tests phishing detection, high-abuse TLDs, and official government whitelists (`cybercrime.gov.in`).
  - 💬 **SMS / Texts:** Evaluates urgency language, utility disconnection scams, and fake lottery rewards.
  - 📱 **QR & UPI:** Detects deceptive "claim refund" transaction notes that trick victims into authorizing debits.
  - 📞 **Phone Numbers:** Looks up numbers against verified fraud databases and community reports.
  - 🎙️ **Voice / Transcripts:** Identifies digital arrest extortion and fake police/CBI officer claims.
- **Threat Intel Feed (`/community`):** Live crowdsourced incident feed with upvote/downvote and report submission modal.
- **User Dashboard (`/dashboard`):** Personal cyber hygiene score (0–100) and paginated scan logs with archival options.
- **Admin Portal (`/admin`):** Threat telemetry counters, incident moderation queue, and system thresholds.
- **Docs & Viva Voce Guide (`/docs`):** Complete REST API endpoint reference and top examination questions with answers.

---

## 🔌 Core REST API Endpoints

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Public | Register new user account |
| `POST` | `/api/v1/auth/login` | Public | Authenticate and obtain JWT token |
| `GET` | `/api/v1/auth/me` | Authenticated | Retrieve authenticated user profile |
| `POST` | `/api/v1/analysis/scan-public` | Public | Instant threat scan without authentication |
| `POST` | `/api/v1/analysis/scan` | Authenticated | Threat scan with audit history recording |
| `GET` | `/api/v1/analysis/history` | Authenticated | Paginated history of scans for current user |
| `DELETE` | `/api/v1/analysis/{id}` | Authenticated | Archive a scan entry |
| `GET` | `/api/v1/community/feed` | Public | Verified crowdsourced incident alerts |
| `POST` | `/api/v1/community/reports` | Public/Auth | Submit a new fraud incident report |
| `POST` | `/api/v1/community/reports/{id}/upvote` | Public | Upvote a threat report (triggers auto-verification at 5 upvotes) |
| `GET` | `/api/v1/admin/analytics` | `ROLE_ADMIN` | Platform metrics and scan telemetry |
| `PATCH`| `/api/v1/admin/reports/{id}/status` | `ROLE_ADMIN` | Moderate community report (`VERIFIED` / `REJECTED`) |

---

## 🐬 Running with Enterprise MySQL (Optional)
To run against a MySQL database instead of the embedded H2 engine:
1. Ensure MySQL is running on `localhost:3306` with database `authentica_db`.
2. Execute:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```
*(Settings can be adjusted in [`ajava/src/main/resources/application-mysql.properties`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/application-mysql.properties)).*
