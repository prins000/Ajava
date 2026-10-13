# 🛠️ Complete Setup & Execution Guide

### Authentica: AI-Powered Scam, Fraud & Cyber Threat Detection Platform
**Advanced Java Programming (GTU BE05000131)**

This guide provides a complete, step-by-step walkthrough to set up and run the Authentica Spring Boot project on a fresh **Windows 10 / 11** machine.

---

## 📋 System Requirements Overview

| Component | Required Version | Purpose |
|---|---|---|
| **Operating System** | Windows 10 or Windows 11 (64-bit) | Host OS |
| **Java Development Kit (JDK)** | **Java 17** or **Java 21** *(Spring Boot 3 requires Java 17 minimum)* | Compiling & Running Java code |
| **Apache Maven** | **Maven 3.8.x** or **3.9.x** | Dependency management & project build |
| **Database** | Embedded **H2 Database** (Included automatically) | Zero setup required; runs in-memory |
| **Web Browser** | Google Chrome, Microsoft Edge, or Mozilla Firefox | Accessing the web interface |

---

## ☕ PART 1: Java (JDK 17) Download & Path Setup

Spring Boot 3 requires **Java 17** or higher. Java 8 or Java 11 will **not** work.

### Step 1.1: Download Java JDK 17
1. Visit the official Eclipse Adoptium (Temurin JDK 17) download page:
   👉 **https://adoptium.net/temurin/releases/?version=17**
   *(Alternatively, download Oracle JDK 17 from https://www.oracle.com/java/technologies/downloads/#java17)*
2. Select:
   - **Operating System:** Windows
   - **Architecture:** x64
   - **Package Type:** **.msi** (Windows Installer)
3. Click **Download** to save the installer file (e.g., `OpenJDK17U-jdk_x64_windows_hotspot_17.0.x.msi`).

---

### Step 1.2: Install JDK 17
1. Double-click the downloaded `.msi` file to run the installer.
2. Click **Next**.
3. Default installation folder is usually:
   ```text
   C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot\
   ```
   *(Or for Oracle JDK: `C:\Program Files\Java\jdk-17`)*
4. If you see the option **"Set JAVA_HOME variable"** in the installer tree, select **"Will be installed on local hard drive"**.
5. Click **Next** -> **Install** -> **Finish**.

---

### Step 1.3: Set Java Environment Variables in Windows

If the installer did not set the path automatically, set it manually:

1. Press `Windows Key + S`, type **Environment Variables**, and click **"Edit the system environment variables"**.
2. Click the **"Environment Variables..."** button at the bottom right.
3. Under the **"System variables"** section (bottom box):
   - Click **New...**
   - **Variable name:** `JAVA_HOME`
   - **Variable value:** `C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot`  
     *(Ensure this matches your actual installed folder path; do NOT include the `\bin` subfolder here).*
   - Click **OK**.
4. Now, find the variable named **`Path`** under **"System variables"**:
   - Select **`Path`** and click **Edit...**
   - Click **New** on the right side.
   - Enter: `%JAVA_HOME%\bin`
   - Click **OK** on all dialog boxes to save changes.

---

### Step 1.4: Verify Java Installation
Open a **new** Command Prompt (`cmd`) or PowerShell window and run:

```bash
java -version
```
**Expected Output:**
```text
openjdk version "17.0.x" ...
OpenJDK Runtime Environment ...
```

Run compiler check:
```bash
javac -version
```
**Expected Output:**
```text
javac 17.0.x
```

---

## 📦 PART 2: Apache Maven Download & Path Setup

Maven is used to download project dependencies, compile classes, and launch the Spring Boot server.

### Step 2.1: Download Apache Maven
1. Open your browser and go to:
   👉 **https://maven.apache.org/download.cgi**
2. Scroll to the **Files** section.
3. Download the **Binary zip archive**:
   - File name format: `apache-maven-3.9.x-bin.zip`

---

### Step 2.2: Extract Apache Maven
1. Locate the downloaded `.zip` file in your `Downloads` folder.
2. Right-click and choose **Extract All...**.
3. Move the extracted folder to a permanent location, for example:
   ```text
   C:\Program Files\apache-maven-3.9.6
   ```
   *(or `C:\apache-maven`)*
4. Inside this folder, you should see subfolders like `bin`, `boot`, `conf`, `lib`.

---

### Step 2.3: Set Maven Environment Variables in Windows

1. Press `Windows Key + S`, type **Environment Variables**, and open **"Edit the system environment variables"**.
2. Click **"Environment Variables..."**.
3. Under **"System variables"** (bottom section):
   - Click **New...**
   - **Variable name:** `MAVEN_HOME`
   - **Variable value:** `C:\Program Files\apache-maven-3.9.6`  
     *(Enter your exact Maven extraction directory path).*
   - Click **OK**.
4. In the same **"System variables"** list, find and select **`Path`**:
   - Click **Edit...**
   - Click **New**
   - Enter: `%MAVEN_HOME%\bin`
   - Click **OK** on all open windows to apply the changes.

---

### Step 2.4: Verify Maven Installation
Close any existing terminal windows, open a **brand new** Command Prompt or PowerShell, and run:

```bash
mvn -version
```

**Expected Output:**
```text
Apache Maven 3.9.x ...
Maven home: C:\Program Files\apache-maven-3.9.x
Java version: 17.0.x, vendor: Eclipse Adoptium
```
*(Ensure it displays Java version 17 or higher).*

---

## 💻 PART 3: Run the Project (Both Methods)

Open Command Prompt or PowerShell and navigate to the project directory:

```bash
cd "c:\Users\Shiyani Prins\OneDrive\Desktop\a java submission\ajava"
```
*(Or if you opened the root directory, simply run `cd ajava`).*

---

### 🚀 METHOD 1: Run Project Directly via Maven (Recommended for Development & Viva)

This single command cleans old artifacts, downloads dependencies, compiles the code, and boots up the embedded application server:

```bash
mvn clean spring-boot:run
```

#### What happens:
1. Maven reads `pom.xml` and downloads required libraries (Spring Boot, Thymeleaf, Spring Security, H2).
2. The internal Spring Boot application starts.
3. The embedded H2 database initializes with seed data (`schema.sql` and `data.sql`).
4. When you see:
   ```text
   Started AuthenticaApplication in X.XXX seconds (process running for X.XXX)
   ```
   The application is running and listening on port `8080`. Keep this terminal window **open**.

---

## 🌐 PART 4: Access the Application

Once either method shows that the application has started, open your web browser:

### 1. Main Web Application
👉 **http://localhost:8080**

### 2. Available User Accounts for Viva / Evaluation:

| Role | Email / Username | Password | Access Details |
|---|---|---|---|
| **Administrator** | `admin@authentica.io` | `Admin@1234` | Full access to Admin Console, Threat Database Moderation, and Telemetry |
| **Citizen / Regular User** | `user@authentica.io` | `User@1234` | Access to Scanner Studio, Community Reports, Scan History & Trust Score |

### 3. Embedded Database Console (H2):
👉 **http://localhost:8080/h2-console**
- **JDBC URL:** `jdbc:h2:mem:authenticadb`
- **User Name:** `sa`
- **Password:** *(Leave blank / empty)*
- Click **Connect** to inspect all live tables (`users`, `scans`, `reports`, `audit_logs`).

---

## 🛑 How to Stop the Project

To stop the running application at any time:
- In the Command Prompt / PowerShell window where the project is running, press:
  ```text
  Ctrl + C
  ```
- Type `Y` (Yes) when prompted to terminate batch job.

---

## 🔧 PART 5: Troubleshooting & Common Issues

### Issue 1: `'mvn'` or `'java'` is not recognized as an internal or external command
- **Cause:** Environment variables were not updated or terminal was opened before saving variables.
- **Fix:**
  1. Ensure `%JAVA_HOME%\bin` and `%MAVEN_HOME%\bin` are added to the **Path** variable in System Environment Variables.
  2. Close all terminal windows and open a **fresh** Command Prompt.

---

### Issue 2: `Port 8080 is already in use`
- **Cause:** Another process (like Tomcat, Jenkins, Oracle XE, or a previous run) is using port 8080.
- **Fix Option A (Run on a different port):**
  ```bash
  mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8085"
  ```
  Or with JAR:
  ```bash
  java -jar target/authentica-platform-1.0.0.jar --server.port=8085
  ```
  Then open: `http://localhost:8085`

- **Fix Option B (Free port 8080 in Windows PowerShell):**
  ```powershell
  Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force
  ```

---

### Issue 3: `UnsupportedClassVersionError: class file has wrong version (61.0)`
- **Cause:** Trying to run the project using Java 8 (version 52.0) or Java 11 (version 55.0). Spring Boot 3 requires Java 17 (version 61.0).
- **Fix:**
  Run `java -version`. Ensure it outputs `17.x.x` or higher. Recheck your `JAVA_HOME` path setting.

---

## 📝 Summary of Quick Commands

```bash
# 1. Check Versions
java -version
mvn -version

# 2. Go to project directory
cd ajava

# 3. RUN (Option 1 - Direct Maven):
mvn clean spring-boot:run

# 4. RUN (Option 2 - Build & Run JAR):
mvn clean package -DskipTests
java -jar target/authentica-platform-1.0.0.jar
```
