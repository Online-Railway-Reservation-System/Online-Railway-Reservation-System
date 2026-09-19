CREATE TABLE IF NOT EXISTS stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_code VARCHAR(10) NOT NULL UNIQUE,
    station_name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    active_status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS route_stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    station_code VARCHAR(10) NOT NULL,
    station_name VARCHAR(100) NOT NULL,
    stop_sequence INT NOT NULL,
    distance_from_origin_km DOUBLE NOT NULL,
    is_source BOOLEAN NOT NULL DEFAULT FALSE,
    is_destination BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_train_seq (train_id, stop_sequence)
);

INSERT INTO stations (station_code, station_name, city, state, active_status)
SELECT 'MAS', 'Chennai Central', 'Chennai', 'Tamil Nadu', true
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_code = 'MAS');

INSERT INTO stations (station_code, station_name, city, state, active_status)
SELECT 'KPD', 'Katpadi Junction', 'Vellore', 'Tamil Nadu', true
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_code = 'KPD');

INSERT INTO stations (station_code, station_name, city, state, active_status)
SELECT 'SA', 'Salem Junction', 'Salem', 'Tamil Nadu', true
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_code = 'SA');

INSERT INTO stations (station_code, station_name, city, state, active_status)
SELECT 'ED', 'Erode Junction', 'Erode', 'Tamil Nadu', true
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_code = 'ED');

INSERT INTO stations (station_code, station_name, city, state, active_status)
SELECT 'CBE', 'Coimbatore Junction', 'Coimbatore', 'Tamil Nadu', true
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_code = 'CBE');

INSERT INTO route_stations (train_id, station_code, station_name, stop_sequence, distance_from_origin_km, is_source, is_destination)
SELECT 1, 'MAS', 'Chennai Central', 1, 0.0, true, false
WHERE NOT EXISTS (SELECT 1 FROM route_stations WHERE train_id = 1 AND station_code = 'MAS');

INSERT INTO route_stations (train_id, station_code, station_name, stop_sequence, distance_from_origin_km, is_source, is_destination)
SELECT 1, 'KPD', 'Katpadi Junction', 2, 130.0, false, false
WHERE NOT EXISTS (SELECT 1 FROM route_stations WHERE train_id = 1 AND station_code = 'KPD');

INSERT INTO route_stations (train_id, station_code, station_name, stop_sequence, distance_from_origin_km, is_source, is_destination)
SELECT 1, 'SA', 'Salem Junction', 3, 334.0, false, false
WHERE NOT EXISTS (SELECT 1 FROM route_stations WHERE train_id = 1 AND station_code = 'SA');

INSERT INTO route_stations (train_id, station_code, station_name, stop_sequence, distance_from_origin_km, is_source, is_destination)
SELECT 1, 'ED', 'Erode Junction', 4, 394.0, false, false
WHERE NOT EXISTS (SELECT 1 FROM route_stations WHERE train_id = 1 AND station_code = 'ED');

INSERT INTO route_stations (train_id, station_code, station_name, stop_sequence, distance_from_origin_km, is_source, is_destination)
SELECT 1, 'CBE', 'Coimbatore Junction', 5, 495.0, false, true
WHERE NOT EXISTS (SELECT 1 FROM route_stations WHERE train_id = 1 AND station_code = 'CBE');