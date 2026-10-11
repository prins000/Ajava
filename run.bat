@echo off
echo Starting Authentica Spring Boot Platform...
cd /d "%~dp0ajava"
"C:\Users\HP\.m2\wrapper\dists\apache-maven-3.10.0-bin\39q0qgmngdu329psje7bjgne79\apache-maven-3.10.0\bin\mvn.cmd" spring-boot:run
pause
