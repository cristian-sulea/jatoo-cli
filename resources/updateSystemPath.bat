@echo off

setx JATOO_CLI_PATH %CD%
for /f "usebackq tokens=2,*" %%A in (`reg query HKCU\Environment /v PATH`) do set USER_PATH=%%B

set alreadySet=false
for /F %%a in ('reg query HKCU\Environment /v PATH ^| find /i "JATOO_CLI_PATH"') do set alreadySet=true
if /i "%alreadySet%"=="true" goto end

setx Path "%USER_PATH%;%%JATOO_CLI_PATH%%"

:end