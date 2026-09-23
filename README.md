# Distributed Examination Coordination & Monitoring System
## Member 3: Incident Management, Event Logging, Heartbeat, Failure Detection & Recovery

This repository implements the **Distributed Monitoring Subsystem** using Java RMI as part of the Distributed Computing Systems (DCS) course project.

---
   
## 1. System Architecture

```text
               +--------------------------------------------+
               |        CENTRAL MONITORING SERVER           |
               |                                            |
               |  [IncidentManager]   [EventManager]        |
               |          |                   |             |
               |  [HeartbeatManager] <-> [FailureDetector]  |
               |          |                   |             |
               |          +-- [RecoveryManager]             |
               |                                            |
               |      RMI: ExamMonitoringService (1099)     |
               +----------------------+---------------------+
                                      ^
                                      | Java RMI
                      +---------------+---------------+
                      |                               |
             +--------+--------+             +--------+--------+
             |   ROOM-101      |             |   ROOM-102      |
             | [Heartbeat-5s]  |             | [Heartbeat-5s]  |
             | [LocalQueue]    |             | [LocalQueue]    |
             +-----------------+             +-----------------+
```

---

## 2. Distributed Computing Concepts Implemented

1. **Java RMI Remote Invocations**:
   - `RemoteMonitoringService` interface extending `java.rmi.Remote`.
   - `MonitoringServiceImpl` extending `UnicastRemoteObject`.
   - Communication over port 1099 using RMI Registry.

2. **Heartbeat Mechanism**:
   - Room nodes periodically pulse heartbeat signals to the Central Server every 5 seconds.
   - The server maintains a thread-safe `ConcurrentHashMap` of last-seen timestamps.

3. **Timeout-Based Failure Detection**:
   - Background daemon thread runs periodic checks (default 2s interval).
   - If `currentTime - lastHeartbeat > timeoutMs` (default 15s, 4s in demo):
     - Transitions room status from `ONLINE` to `OFFLINE`.
     - Generates and logs a single `ROOM_OFFLINE` audit event (preventing duplicates).

4. **Failure Recovery & State Reconnection**:
   - When an offline room resumes communication, `RecoveryManager` transitions the status back to `ONLINE`.
   - Logs `ROOM_RECONNECTED` event and reconciles room state.

5. **Incident Lifecycle State Machine**:
   - `OPEN` -> `ACKNOWLEDGED` -> `IN_PROGRESS` -> `RESOLVED`.
   - Automatically publishes `INCIDENT_REPORTED` and `INCIDENT_RESOLVED` events.

6. **Local Event Queue (Disconnection Resilience)**:
   - Client-side queue buffers generated events locally if RMI server is temporarily unreachable.
   - Flushes buffered events automatically when connectivity is restored.

7. **Database Decoupling (DAO Layer)**:
   - Clean interfaces (`IncidentDAO`, `EventDAO`) with `InMemory` implementations.
   - Ready for drop-in replacement with Member 4's PostgreSQL JDBC DAO.

---

## 3. How to Build and Run

### Step 1: Compile the Project
```cmd
build.bat
```
*(Or manually: `javac -d bin src/com/exam/**/*.java`)*

### Step 2: Run End-to-End Automated Demonstration Test
```cmd
run_simulation.bat
```
*(Or manually: `java -cp bin com.exam.test.SimulationDemo`)*

### Step 3: Run Interactive Multi-Node Distributed System
**Terminal 1 — Central Server:**
```cmd
run_server.bat
```

**Terminal 2 — Room 101 Client:**
```cmd
run_room1.bat
```

**Terminal 3 — Room 102 Client:**
```cmd
run_room2.bat
```

---

## 4. Failure Demonstration Sequence (For Viva / Demo)

1. **Start Central Server**: Starts RMI registry and Failure Detector.
2. **Start Room 101 & Room 102**: Both connect, register, and pulse heartbeats.
3. **Report Incident**: In Room 101 console, press `1` or `2` to report an incident. Server displays incident audit log.
4. **Simulate Failure**: In Room 101 console, press `4` (stops heartbeats).
   - Room 102 continues pulsing heartbeats normally.
5. **Detect Failure**: Within 15 seconds, Central Server alerts:
   - `Room ROOM-101 missed heartbeats -> MARKED OFFLINE`
   - Generates `ROOM_OFFLINE` event.
6. **Simulate Recovery**: In Room 101 console, press `5` (resumes heartbeats).
   - Central Server detects pulse -> `ROOM-101 recovered -> NOW ONLINE`
   - Generates `ROOM_RECONNECTED` event.

# Exam Monitor System (Member 2 Module)

This repository contains the backend RMI services for the Distributed Examination Coordination & Monitoring System.

## Architecture
- RMI Server exposing services for Exams, Rooms, Students, Attendance, and Submissions.
- Standard DAO layer connected to PostgreSQL.

## How to Compile & Run
1. Update database credentials in `src/main/java/com/distributedexam/dao/DBConnection.java`
2. Run `powershell -ExecutionPolicy Bypass -File build.ps1` to compile.
3. Start the server using: `java -cp "bin;lib/*" com.distributedexam.server.RMIServer`
