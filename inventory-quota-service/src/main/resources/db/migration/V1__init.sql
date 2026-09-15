CREATE TABLE IF NOT EXISTS coaches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    coach_number VARCHAR(10) NOT NULL,
    class_type VARCHAR(10) NOT NULL,
    total_seats INT NOT NULL DEFAULT 72,
    active_status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_train_coach (train_id, coach_number)
);

CREATE TABLE IF NOT EXISTS seat_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    coach_number VARCHAR(10) NOT NULL,
    seat_number VARCHAR(20) NOT NULL,
    class_type VARCHAR(10) NOT NULL,
    berth_type VARCHAR(20) NOT NULL,
    quota_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    hold_reference VARCHAR(100) NULL,
    hold_expiry TIMESTAMP NULL,
    reservation_id BIGINT NULL,
    pnr VARCHAR(20) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_train_date_class_quota (train_id, journey_date, class_type, quota_type, status),
    INDEX idx_hold_ref (hold_reference),
    INDEX idx_pnr (pnr)
);

CREATE TABLE IF NOT EXISTS train_quotas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    class_type VARCHAR(10) NOT NULL,
    quota_type VARCHAR(20) NOT NULL,
    allocated_seats INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed coaches for train 1
INSERT INTO coaches (train_id, coach_number, class_type, total_seats, active_status)
SELECT 1, 'S1', 'SL', 72, true
WHERE NOT EXISTS (SELECT 1 FROM coaches WHERE train_id = 1 AND coach_number = 'S1');

INSERT INTO coaches (train_id, coach_number, class_type, total_seats, active_status)
SELECT 1, 'B1', '3A', 72, true
WHERE NOT EXISTS (SELECT 1 FROM coaches WHERE train_id = 1 AND coach_number = 'B1');

INSERT INTO coaches (train_id, coach_number, class_type, total_seats, active_status)
SELECT 1, 'A1', '2A', 54, true
WHERE NOT EXISTS (SELECT 1 FROM coaches WHERE train_id = 1 AND coach_number = 'A1');

-- Seed quotas for train 1
INSERT INTO train_quotas (train_id, class_type, quota_type, allocated_seats)
SELECT 1, 'SL', 'GENERAL', 50
WHERE NOT EXISTS (SELECT 1 FROM train_quotas WHERE train_id = 1 AND class_type = 'SL' AND quota_type = 'GENERAL');

INSERT INTO train_quotas (train_id, class_type, quota_type, allocated_seats)
SELECT 1, 'SL', 'TATKAL', 15
WHERE NOT EXISTS (SELECT 1 FROM train_quotas WHERE train_id = 1 AND class_type = 'SL' AND quota_type = 'TATKAL');

INSERT INTO train_quotas (train_id, class_type, quota_type, allocated_seats)
SELECT 1, 'SL', 'LADIES', 7
WHERE NOT EXISTS (SELECT 1 FROM train_quotas WHERE train_id = 1 AND class_type = 'SL' AND quota_type = 'LADIES');