CREATE TABLE IF NOT EXISTS train_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    departure_station_code VARCHAR(10) NOT NULL,
    arrival_station_code VARCHAR(10) NOT NULL,
    departure_time VARCHAR(10) NOT NULL,
    arrival_time VARCHAR(10) NOT NULL,
    running_days VARCHAR(50) NOT NULL,
    duration_hours DOUBLE NOT NULL,
    valid_from DATE NOT NULL,
    valid_upto DATE NOT NULL,
    active_status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_train_sched (train_id)
);

CREATE TABLE IF NOT EXISTS fare_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NULL,
    class_type VARCHAR(10) NOT NULL,
    base_fare_per_km DOUBLE NOT NULL,
    minimum_fare DOUBLE NOT NULL,
    reservation_fee DOUBLE NOT NULL,
    superfast_charge DOUBLE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_class_train (class_type, train_id)
);

CREATE TABLE IF NOT EXISTS tatkal_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL UNIQUE,
    tatkal_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    advance_days INT NOT NULL DEFAULT 1,
    ac_opening_time VARCHAR(10) NOT NULL DEFAULT '10:00',
    non_ac_opening_time VARCHAR(10) NOT NULL DEFAULT '11:00',
    surcharge_percentage DOUBLE NOT NULL DEFAULT 30.0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Seed initial schedule for train 1
INSERT INTO train_schedules (train_id, departure_station_code, arrival_station_code, departure_time, arrival_time, running_days, duration_hours, valid_from, valid_upto, active_status)
SELECT 1, 'MAS', 'CBE', '06:10', '13:05', 'MON,TUE,WED,THU,FRI,SAT,SUN', 6.91, '2024-01-01', '2027-12-31', true
WHERE NOT EXISTS (SELECT 1 FROM train_schedules WHERE train_id = 1);

-- Seed fare rules for standard classes
INSERT INTO fare_rules (train_id, class_type, base_fare_per_km, minimum_fare, reservation_fee, superfast_charge)
SELECT NULL, '1A', 3.0, 700.0, 60.0, 75.0
WHERE NOT EXISTS (SELECT 1 FROM fare_rules WHERE class_type = '1A' AND train_id IS NULL);

INSERT INTO fare_rules (train_id, class_type, base_fare_per_km, minimum_fare, reservation_fee, superfast_charge)
SELECT NULL, '2A', 2.0, 450.0, 50.0, 45.0
WHERE NOT EXISTS (SELECT 1 FROM fare_rules WHERE class_type = '2A' AND train_id IS NULL);

INSERT INTO fare_rules (train_id, class_type, base_fare_per_km, minimum_fare, reservation_fee, superfast_charge)
SELECT NULL, '3A', 1.4, 300.0, 40.0, 45.0
WHERE NOT EXISTS (SELECT 1 FROM fare_rules WHERE class_type = '3A' AND train_id IS NULL);

INSERT INTO fare_rules (train_id, class_type, base_fare_per_km, minimum_fare, reservation_fee, superfast_charge)
SELECT NULL, 'SL', 0.8, 140.0, 20.0, 30.0
WHERE NOT EXISTS (SELECT 1 FROM fare_rules WHERE class_type = 'SL' AND train_id IS NULL);

-- Seed Tatkal config for train 1
INSERT INTO tatkal_config (train_id, tatkal_enabled, advance_days, ac_opening_time, non_ac_opening_time, surcharge_percentage)
SELECT 1, true, 1, '10:00', '11:00', 30.0
WHERE NOT EXISTS (SELECT 1 FROM tatkal_config WHERE train_id = 1);