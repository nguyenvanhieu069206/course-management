@echo off
cd /d "%~dp0"
echo Dang khoi dong ung dung, vui long doi...
start "Course Management Server" cmd /k ".\mvnw.cmd spring-boot:run"
timeout /t 25 /nobreak
start http://localhost:8080
