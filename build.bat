@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo   COMPILING DCS EXAM MONITOR SYSTEM (MEMBER 3)
echo ===================================================

rem 1. Check for javac in PATH
where javac >nul 2>nul
if %errorlevel% equ 0 (
    set JAVAC_CMD=javac
    goto :COMPILE
)

rem 2. Search D:\PROJECT for portable JDK
for /d %%D in ("D:\PROJECT\jdk*" "D:\PROJECT\bellsoft*") do (
    if exist "%%D\bin\javac.exe" (
        set JAVAC_CMD="%%D\bin\javac.exe"
        goto :COMPILE
    )
)

echo ERROR: javac was not found in PATH or D:\PROJECT\
exit /b 1

:COMPILE
echo Using Java Compiler: %JAVAC_CMD%
if not exist "bin" mkdir "bin"

%JAVAC_CMD% -encoding UTF-8 -d bin src\com\exam\common\model\*.java src\com\exam\common\rmi\*.java src\com\exam\dao\*.java src\com\exam\server\service\*.java src\com\exam\server\*.java src\com\exam\client\service\*.java src\com\exam\client\*.java src\com\exam\test\*.java

if %errorlevel% equ 0 (
    echo [BUILD SUCCESS] All classes compiled into bin\
) else (
    echo [BUILD FAILED] Compilation errors occurred.
    exit /b %errorlevel%
)
