# Exam Monitor System (Member 2 Module)

This repository contains the backend RMI services for the Distributed Examination Coordination & Monitoring System.

## Architecture
- RMI Server exposing services for Exams, Rooms, Students, Attendance, and Submissions.
- Standard DAO layer connected to PostgreSQL.

## How to Compile & Run
1. Update database credentials in `src/main/java/com/distributedexam/dao/DBConnection.java`
2. Run `powershell -ExecutionPolicy Bypass -File build.ps1` to compile.
3. Start the server using: `java -cp "bin;lib/*" com.distributedexam.server.RMIServer`
