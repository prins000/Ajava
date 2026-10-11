# Gujarat Technological University (GTU)
## Course: Advanced Java Programming (BE05000131)
### 04_Source_Code: Project Source Code Package

---

## 1. Source Code Location
The complete, production-ready, compilable Spring Boot source code is located in the **`ajava/`** directory of this workspace:

📂 **Primary Source Code Directory:** [`ajava/`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava)

---

## 2. Java Package Architecture & File Manifest

### A. Root Application & Configuration
- [`ajava/src/main/java/com/authentica/AuthenticaApplication.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/AuthenticaApplication.java): Spring Boot application entrypoint with ASCII banner and startup diagnostics.
- [`ajava/pom.xml`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/pom.xml): Maven build configuration (Spring Boot 3.2.3, JPA, Security, H2, MySQL, JWT, Thymeleaf).
- [`ajava/src/main/resources/application.properties`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/application.properties): Embedded H2 database and JWT security configuration.
- [`ajava/src/main/resources/application-mysql.properties`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/application-mysql.properties): Enterprise MySQL profile.

### B. Domain Models & JPA Entities (`com.authentica.model`)
- [`User.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/User.java): Citizen user entity with BCrypt password, trust score, and risk level.
- [`Analysis.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/Analysis.java): Scam inspection records, flags JSON, and metrics.
- [`CommunityReport.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/CommunityReport.java): Crowdsourced incident reports, upvotes, and verification status.
- [`FraudNumber.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/FraudNumber.java): Blacklisted spam/fraud telephone numbers registry.
- [`FraudURL.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/FraudURL.java): Blacklisted phishing websites and domains registry.
- [`FraudQR.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/FraudQR.java): Malicious UPI QR payment strings.
- [`ProtectedContact.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/ProtectedContact.java): Monitored family contacts.
- [`SupportTicket.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/SupportTicket.java): User assistance requests.
- [`Feedback.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/Feedback.java): User reviews and false-positive submissions.
- [`AppSettings.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/AppSettings.java): System flags and configurable thresholds.
- [`Whitelist.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/model/Whitelist.java): Government portals and verified official institutions.

### C. Spring Data Repositories (`com.authentica.repository`)
- `UserRepository`, `AnalysisRepository`, `CommunityReportRepository`, `FraudNumberRepository`, `FraudURLRepository`, `FraudQRRepository`, `ProtectedContactRepository`, `SupportTicketRepository`, `FeedbackRepository`, `AppSettingsRepository`, `WhitelistRepository`.

### D. Security & JWT Filter Chain (`com.authentica.security`)
- [`JwtTokenProvider.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/security/JwtTokenProvider.java): HMAC-SHA256 signature generation and claims extraction.
- [`JwtAuthenticationFilter.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/security/JwtAuthenticationFilter.java): Request interceptor for Bearer token validation.
- [`CustomUserDetailsService.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/security/CustomUserDetailsService.java): Spring Security user bridge.
- [`SecurityConfig.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/security/SecurityConfig.java): Declarative endpoint security and session policies.

### E. Heuristic Detection Engines (`com.authentica.service.engine`)
- [`UrlAnalysisEngine.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/engine/UrlAnalysisEngine.java): Phishing domain, TLD, and keyword inspector.
- [`TextAnalysisEngine.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/engine/TextAnalysisEngine.java): Linguistic panic and urgency analyzer.
- [`QrAnalysisEngine.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/engine/QrAnalysisEngine.java): Deep UPI string and deceptive debit detector.
- [`VoiceAnalysisEngine.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/engine/VoiceAnalysisEngine.java): Digital arrest extortion speech pattern analyzer.
- [`NumberLookupEngine.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/engine/NumberLookupEngine.java): Telephone fraud registry and blacklist lookup.
- [`EngineResult.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/engine/EngineResult.java): Unified threat score, risk level, flags, and recommendation container.

### F. Business Services (`com.authentica.service`)
- [`AuthService.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/AuthService.java): Registration, authentication, token issuance.
- [`AnalysisService.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/AnalysisService.java): Engine coordination and risk score updates.
- [`CommunityReportService.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/CommunityReportService.java): Incident voting and automatic intelligence synchronization.
- [`AdminService.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/AdminService.java): Administrative telemetry and configuration.
- [`DataInitializerService.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/service/DataInitializerService.java): Startup demo data seeder.

### G. REST Controllers & Web Views (`com.authentica.controller`)
- [`AuthController.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/controller/AuthController.java): `/api/v1/auth/*`
- [`AnalysisController.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/controller/AnalysisController.java): `/api/v1/analysis/*`
- [`CommunityReportController.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/controller/CommunityReportController.java): `/api/v1/community/*`
- [`AdminController.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/controller/AdminController.java): `/api/v1/admin/*`
- [`WebViewController.java`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/java/com/authentica/controller/WebViewController.java): Routes for HTML dashboard pages.

### H. Web User Interface & Static Assets
- [`ajava/src/main/resources/templates/index.html`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/templates/index.html): Home portal and instant threat analyzer.
- [`ajava/src/main/resources/templates/scanner.html`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/templates/scanner.html): Multi-modal scanner studio with 1-click test examples.
- [`ajava/src/main/resources/templates/community.html`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/templates/community.html): Live threat feed and report submission modal.
- [`ajava/src/main/resources/templates/dashboard.html`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/templates/dashboard.html): Citizen cyber hygiene profile and scan audit history.
- [`ajava/src/main/resources/templates/admin.html`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/templates/admin.html): Administrative moderation console.
- [`ajava/src/main/resources/templates/docs.html`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/templates/docs.html): Interactive API specs and Viva Voce preparation guide.
- [`ajava/src/main/resources/static/css/style.css`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/static/css/style.css): Custom dark-mode cyber theme.
- [`ajava/src/main/resources/static/js/app.js`](file:///c:/Users/Shiyani%20Prins/OneDrive/Desktop/a%20java%20submission/ajava/src/main/resources/static/js/app.js): Client-side state and API interaction layer.

---

## 3. How to Build and Run the Application

### Option A: Standard Build & Launch (H2 In-Memory - Recommended)
```bash
cd ajava
mvn clean spring-boot:run
```
Open **http://localhost:8080/** in any web browser.

### Option B: Build Standalone JAR Package
```bash
cd ajava
mvn clean package -DskipTests
java -jar target/authentica-platform-1.0.0.jar
```

### Option C: Launch with MySQL Database Profile
```bash
cd ajava
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```
*(Ensure a MySQL database named `authentica_db` exists on `localhost:3306` with user `root` / password `root`).*

---

## 4. Default Seeded Test Credentials
- **Administrator Account:** `admin@authentica.io` / `Admin@1234`
- **Standard Citizen Account:** `user@authentica.io` / `User@1234`
- **Embedded Database Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:authenticadb`, User: `sa`, Password: *(empty)*)
