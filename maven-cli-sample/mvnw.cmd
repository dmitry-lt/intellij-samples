@echo off
setlocal
where powershell.exe >nul 2>&1
if errorlevel 1 (
  echo PowerShell is required to download and launch the configured Maven distribution. 1>&2
  exit /b 1
)
powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0.mvn\wrapper\maven-wrapper.ps1" %*
exit /b %ERRORLEVEL%
