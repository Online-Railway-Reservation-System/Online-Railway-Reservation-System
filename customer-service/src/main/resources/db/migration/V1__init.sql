CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mobile VARCHAR(20),
    address VARCHAR(255),
    gender VARCHAR(10),
    date_of_birth DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS concessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    concession_type VARCHAR(50) NOT NULL,
    concession_number VARCHAR(50) NOT NULL,
    valid_from DATE NOT NULL,
    valid_upto DATE NOT NULL,
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    verified_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cust_id (customer_id)
);

CREATE TABLE IF NOT EXISTS concession_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    concession_number VARCHAR(50) NOT NULL UNIQUE,
    concession_type VARCHAR(50) NOT NULL,
    holder_name VARCHAR(100) NOT NULL,
    holder_identifier VARCHAR(100),
    valid_from DATE NOT NULL,
    valid_upto DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customer_queries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    admin_reply TEXT,
    replied_by VARCHAR(100),
    replied_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);

-- Seed memorable Concession IDs (15 per department)
-- 1. Senior Citizen (SEN-101 to SEN-115)
INSERT IGNORE INTO concession_verification (concession_number, concession_type, holder_name, holder_identifier, valid_from, valid_upto, status) VALUES
('SEN-101', 'SENIOR_CITIZEN', 'Senior Citizen 101', 'REG-SEN-101', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-102', 'SENIOR_CITIZEN', 'Senior Citizen 102', 'REG-SEN-102', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-103', 'SENIOR_CITIZEN', 'Senior Citizen 103', 'REG-SEN-103', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-104', 'SENIOR_CITIZEN', 'Senior Citizen 104', 'REG-SEN-104', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-105', 'SENIOR_CITIZEN', 'Senior Citizen 105', 'REG-SEN-105', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-106', 'SENIOR_CITIZEN', 'Senior Citizen 106', 'REG-SEN-106', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-107', 'SENIOR_CITIZEN', 'Senior Citizen 107', 'REG-SEN-107', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-108', 'SENIOR_CITIZEN', 'Senior Citizen 108', 'REG-SEN-108', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-109', 'SENIOR_CITIZEN', 'Senior Citizen 109', 'REG-SEN-109', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-110', 'SENIOR_CITIZEN', 'Senior Citizen 110', 'REG-SEN-110', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-111', 'SENIOR_CITIZEN', 'Senior Citizen 111', 'REG-SEN-111', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-112', 'SENIOR_CITIZEN', 'Senior Citizen 112', 'REG-SEN-112', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-113', 'SENIOR_CITIZEN', 'Senior Citizen 113', 'REG-SEN-113', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-114', 'SENIOR_CITIZEN', 'Senior Citizen 114', 'REG-SEN-114', '2023-01-01', '2030-12-31', 'ACTIVE'),
('SEN-115', 'SENIOR_CITIZEN', 'Senior Citizen 115', 'REG-SEN-115', '2023-01-01', '2030-12-31', 'ACTIVE');

-- 2. Student (STU-101 to STU-115)
INSERT IGNORE INTO concession_verification (concession_number, concession_type, holder_name, holder_identifier, valid_from, valid_upto, status) VALUES
('STU-101', 'STUDENT', 'Student Scholar 101', 'REG-STU-101', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-102', 'STUDENT', 'Student Scholar 102', 'REG-STU-102', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-103', 'STUDENT', 'Student Scholar 103', 'REG-STU-103', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-104', 'STUDENT', 'Student Scholar 104', 'REG-STU-104', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-105', 'STUDENT', 'Student Scholar 105', 'REG-STU-105', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-106', 'STUDENT', 'Student Scholar 106', 'REG-STU-106', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-107', 'STUDENT', 'Student Scholar 107', 'REG-STU-107', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-108', 'STUDENT', 'Student Scholar 108', 'REG-STU-108', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-109', 'STUDENT', 'Student Scholar 109', 'REG-STU-109', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-110', 'STUDENT', 'Student Scholar 110', 'REG-STU-110', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-111', 'STUDENT', 'Student Scholar 111', 'REG-STU-111', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-112', 'STUDENT', 'Student Scholar 112', 'REG-STU-112', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-113', 'STUDENT', 'Student Scholar 113', 'REG-STU-113', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-114', 'STUDENT', 'Student Scholar 114', 'REG-STU-114', '2023-01-01', '2030-12-31', 'ACTIVE'),
('STU-115', 'STUDENT', 'Student Scholar 115', 'REG-STU-115', '2023-01-01', '2030-12-31', 'ACTIVE');

-- 3. Differently Abled (DIS-101 to DIS-115)
INSERT IGNORE INTO concession_verification (concession_number, concession_type, holder_name, holder_identifier, valid_from, valid_upto, status) VALUES
('DIS-101', 'DISABLED', 'Special Needs 101', 'REG-DIS-101', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-102', 'DISABLED', 'Special Needs 102', 'REG-DIS-102', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-103', 'DISABLED', 'Special Needs 103', 'REG-DIS-103', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-104', 'DISABLED', 'Special Needs 104', 'REG-DIS-104', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-105', 'DISABLED', 'Special Needs 105', 'REG-DIS-105', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-106', 'DISABLED', 'Special Needs 106', 'REG-DIS-106', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-107', 'DISABLED', 'Special Needs 107', 'REG-DIS-107', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-108', 'DISABLED', 'Special Needs 108', 'REG-DIS-108', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-109', 'DISABLED', 'Special Needs 109', 'REG-DIS-109', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-110', 'DISABLED', 'Special Needs 110', 'REG-DIS-110', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-111', 'DISABLED', 'Special Needs 111', 'REG-DIS-111', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-112', 'DISABLED', 'Special Needs 112', 'REG-DIS-112', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-113', 'DISABLED', 'Special Needs 113', 'REG-DIS-113', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-114', 'DISABLED', 'Special Needs 114', 'REG-DIS-114', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DIS-115', 'DISABLED', 'Special Needs 115', 'REG-DIS-115', '2023-01-01', '2030-12-31', 'ACTIVE');

-- 4. Defence Personnel (DEF-101 to DEF-115)
INSERT IGNORE INTO concession_verification (concession_number, concession_type, holder_name, holder_identifier, valid_from, valid_upto, status) VALUES
('DEF-101', 'DEFENCE', 'Armed Forces 101', 'REG-DEF-101', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-102', 'DEFENCE', 'Armed Forces 102', 'REG-DEF-102', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-103', 'DEFENCE', 'Armed Forces 103', 'REG-DEF-103', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-104', 'DEFENCE', 'Armed Forces 104', 'REG-DEF-104', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-105', 'DEFENCE', 'Armed Forces 105', 'REG-DEF-105', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-106', 'DEFENCE', 'Armed Forces 106', 'REG-DEF-106', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-107', 'DEFENCE', 'Armed Forces 107', 'REG-DEF-107', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-108', 'DEFENCE', 'Armed Forces 108', 'REG-DEF-108', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-109', 'DEFENCE', 'Armed Forces 109', 'REG-DEF-109', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-110', 'DEFENCE', 'Armed Forces 110', 'REG-DEF-110', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-111', 'DEFENCE', 'Armed Forces 111', 'REG-DEF-111', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-112', 'DEFENCE', 'Armed Forces 112', 'REG-DEF-112', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-113', 'DEFENCE', 'Armed Forces 113', 'REG-DEF-113', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-114', 'DEFENCE', 'Armed Forces 114', 'REG-DEF-114', '2023-01-01', '2030-12-31', 'ACTIVE'),
('DEF-115', 'DEFENCE', 'Armed Forces 115', 'REG-DEF-115', '2023-01-01', '2030-12-31', 'ACTIVE');