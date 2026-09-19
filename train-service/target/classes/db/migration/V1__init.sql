CREATE TABLE IF NOT EXISTS trains (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_number VARCHAR(20) NOT NULL UNIQUE,
    train_name VARCHAR(100) NOT NULL,
    train_type VARCHAR(50) NOT NULL,
    active_status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO trains (train_number, train_name, train_type, active_status)
SELECT '12678', 'Cheran Superfast Express', 'SUPERFAST', true
WHERE NOT EXISTS (SELECT 1 FROM trains WHERE train_number = '12678');

INSERT INTO trains (train_number, train_name, train_type, active_status)
SELECT '12007', 'Chennai Shatabdi Express', 'SHATABDI', true
WHERE NOT EXISTS (SELECT 1 FROM trains WHERE train_number = '12007');

INSERT INTO trains (train_number, train_name, train_type, active_status)
SELECT '12601', 'Mangalore Superfast Mail', 'EXPRESS', true
WHERE NOT EXISTS (SELECT 1 FROM trains WHERE train_number = '12601');