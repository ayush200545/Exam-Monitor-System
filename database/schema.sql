-- Database schema for Distributed Examination Coordination & Monitoring System

-- Drop tables if they exist to allow easy re-creation
DROP TABLE IF EXISTS submissions;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS exam_students;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS exams;

-- Exams Table
CREATE TABLE exams (
    exam_id SERIAL PRIMARY KEY,
    exam_name VARCHAR(255) NOT NULL,
    subject_code VARCHAR(100) NOT NULL,
    exam_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_minutes INT NOT NULL,
    status VARCHAR(50) NOT NULL, -- SCHEDULED, RUNNING, COMPLETED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms Table
CREATE TABLE rooms (
    room_id SERIAL PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    location VARCHAR(255),
    status VARCHAR(50) NOT NULL, -- AVAILABLE, ASSIGNED, EXAM_RUNNING, OFFLINE, MAINTENANCE
    current_exam_id INT,
    FOREIGN KEY (current_exam_id) REFERENCES exams(exam_id) ON DELETE SET NULL
);

-- Students Table
CREATE TABLE students (
    student_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    department VARCHAR(100),
    semester VARCHAR(50)
);

-- Exam-Student Mapping Table
CREATE TABLE exam_students (
    exam_id INT NOT NULL,
    student_id INT NOT NULL,
    room_id INT NOT NULL,
    seat_number VARCHAR(50),
    PRIMARY KEY (exam_id, student_id),
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

-- Attendance Table
CREATE TABLE attendance (
    attendance_id SERIAL PRIMARY KEY,
    exam_id INT NOT NULL,
    student_id INT NOT NULL,
    room_id INT NOT NULL,
    status VARCHAR(50) NOT NULL, -- PRESENT, ABSENT
    marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    marked_by VARCHAR(255),
    UNIQUE (exam_id, student_id), -- Prevent duplicate attendance
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

-- Submissions Table
CREATE TABLE submissions (
    submission_id SERIAL PRIMARY KEY,
    exam_id INT NOT NULL,
    student_id INT NOT NULL,
    room_id INT NOT NULL,
    status VARCHAR(50) NOT NULL, -- NOT_SUBMITTED, SUBMITTED, LATE
    submitted_at TIMESTAMP,
    UNIQUE (exam_id, student_id),
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

-- =========================================================
-- USERS
-- =========================================================

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,

    username VARCHAR(100) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    role VARCHAR(50) NOT NULL,

    room_id INT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
        ON DELETE SET NULL
);



-- =========================================================
-- INCIDENTS
-- =========================================================

CREATE TABLE incidents (
    incident_id SERIAL PRIMARY KEY,

    exam_id INT,
    room_id INT,
    student_id INT,

    type VARCHAR(100) NOT NULL,

    severity VARCHAR(30) NOT NULL,

    description TEXT NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    resolved_at TIMESTAMP,

    FOREIGN KEY (exam_id)
        REFERENCES exams(exam_id)
        ON DELETE SET NULL,

    FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
        ON DELETE SET NULL,

    FOREIGN KEY (student_id)
        REFERENCES students(student_id)
        ON DELETE SET NULL
);


-- =========================================================
-- EXAM EVENTS
-- =========================================================

CREATE TABLE exam_events (
    event_id SERIAL PRIMARY KEY,

    exam_id INT,

    room_id INT,

    event_type VARCHAR(100) NOT NULL,

    description TEXT,

    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    user_id INT,

    FOREIGN KEY (exam_id)
        REFERENCES exams(exam_id)
        ON DELETE SET NULL,

    FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)
        ON DELETE SET NULL,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
);