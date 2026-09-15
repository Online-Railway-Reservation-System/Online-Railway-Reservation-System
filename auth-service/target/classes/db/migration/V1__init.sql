CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    mobile VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    INDEX idx_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS password_reset_otps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    otp VARCHAR(10) NOT NULL,
    expiry_time TIMESTAMP NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_email (email)
);

-- Seed initial admin and customer (password is 'Password@123' bcrypt hashed)
INSERT INTO users (email, password, full_name, mobile, role, active)
SELECT 'admin@railway.com', '$2a$10$7R0Oa1GqU8tG5Uj0K1M.nO6xX/6zQZvZ2gQhQYpYmOe2yM.PqyS1G', 'Railway Admin', '9876543210', 'ADMIN', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@railway.com');

INSERT INTO users (email, password, full_name, mobile, role, active)
SELECT 'customer@railway.com', '$2a$10$7R0Oa1GqU8tG5Uj0K1M.nO6xX/6zQZvZ2gQhQYpYmOe2yM.PqyS1G', 'Test Customer', '9123456780', 'CUSTOMER', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'customer@railway.com');