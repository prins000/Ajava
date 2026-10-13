@echo off
echo ========================================================
echo Starting Authentica Spring Boot Platform...
echo Zero-configuration H2 Database mode on port 8080
echo ========================================================
cd /d "%~dp0ajava"
call mvnw.cmd spring-boot:run
pause

