@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo   STARTING EXAM ROOM CLIENT - ROOM-101
echo ===================================================

where java >nul 2>nul
if %errorlevel% equ 0 (
    set JAVA_CMD=java
    goto :RUN
)

for /d %%D in ("D:\PROJECT\jdk*" "D:\PROJECT\bellsoft*") do (
    if exist "%%D\bin\java.exe" (
        set JAVA_CMD="%%D\bin\java.exe"
        goto :RUN
    )
)

echo ERROR: java was not found in PATH or D:\PROJECT\
exit /b 1

:RUN
%JAVA_CMD% -cp bin com.exam.client.RoomClientMain ROOM-101 localhost 1099
