@echo off
cd /d %~dp0
if "%ANTHROPIC_API_KEY%"=="" set /p ANTHROPIC_API_KEY=Paste your Anthropic API key (or press Enter to skip AI chat): 
start "" cmd /c "timeout /t 3 >nul & start http://localhost:8080"
java ChatServer.java
pause
