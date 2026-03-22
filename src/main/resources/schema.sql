-- PYQ Practice Portal - Default data init
-- Safe to re-run: uses CREATE IF NOT EXISTS and INSERT IGNORE

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STUDENT') NOT NULL
);

CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_text TEXT NOT NULL,
    question_type VARCHAR(20) DEFAULT 'MCQ',
    option_a VARCHAR(255),
    option_b VARCHAR(255),
    option_c VARCHAR(255),
    option_d VARCHAR(255),
    correct_answer TEXT,
    attachment_path VARCHAR(500),
    attachment_mime VARCHAR(100),
    attachment_name VARCHAR(255),
    subject_id BIGINT NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

-- Upgrade existing DB (safe-ish; columns may already exist)
-- Make option columns nullable for non-MCQ question types
ALTER TABLE questions
    MODIFY option_a VARCHAR(255) NULL,
    MODIFY option_b VARCHAR(255) NULL,
    MODIFY option_c VARCHAR(255) NULL,
    MODIFY option_d VARCHAR(255) NULL;

-- Ensure new columns exist (ignore errors if already present)
ALTER TABLE questions ADD COLUMN question_type VARCHAR(20) DEFAULT 'MCQ';
ALTER TABLE questions ADD COLUMN attachment_path VARCHAR(500);
ALTER TABLE questions ADD COLUMN attachment_mime VARCHAR(100);
ALTER TABLE questions ADD COLUMN attachment_name VARCHAR(255);

-- correct_answer may have been VARCHAR(1); widen to TEXT
ALTER TABLE questions MODIFY correct_answer TEXT NULL;

CREATE TABLE IF NOT EXISTS test_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    total_questions INT DEFAULT 0,
    correct_answers INT DEFAULT 0,
    wrong_answers INT DEFAULT 0,
    score DOUBLE DEFAULT 0,
    attempt_date DATETIME,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id)
);

CREATE TABLE IF NOT EXISTS answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option VARCHAR(255),
    is_correct BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (attempt_id) REFERENCES test_attempts(id),
    FOREIGN KEY (question_id) REFERENCES questions(id)
);

-- Allow text answers too
ALTER TABLE answers MODIFY selected_option VARCHAR(255);

-- Default admin account (password: admin123)
INSERT IGNORE INTO users (name, email, password, role)
VALUES ('Admin', 'admin@pyq.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');
