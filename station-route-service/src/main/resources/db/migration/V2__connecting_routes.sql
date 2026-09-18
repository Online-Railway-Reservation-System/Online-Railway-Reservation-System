-- Add New Delhi and intermediate junction stations for connecting route demonstration
INSERT INTO stations (station_code, station_name, city, state, active_status)
SELECT 'NDLS', 'New Delhi', 'Delhi', 'Delhi', true
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_code = 'NDLS');

INSERT INTO stations (station_code, station_name, city, state, active_status)
SELECT 'BZA', 'Vijayawada Junction', 'Vijayawada', 'Andhra Pradesh', true
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_code = 'BZA');

INSERT INTO stations (station_code, station_name, city, state, active_status)
SELECT 'NGP', 'Nagpur Junction', 'Nagpur', 'Maharashtra', true
WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_code = 'NGP');

-- Route stops for Train 2 (MAS -> NDLS)
INSERT INTO route_stations (train_id, station_code, station_name, stop_sequence, distance_from_origin_km, is_source, is_destination)
SELECT 2, 'MAS', 'Chennai Central', 1, 0.0, true, false
WHERE NOT EXISTS (SELECT 1 FROM route_stations WHERE train_id = 2 AND station_code = 'MAS');

INSERT INTO route_stations (train_id, station_code, station_name, stop_sequence, distance_from_origin_km, is_source, is_destination)
SELECT 2, 'BZA', 'Vijayawada Junction', 2, 431.0, false, false
WHERE NOT EXISTS (SELECT 1 FROM route_stations WHERE train_id = 2 AND station_code = 'BZA');

INSERT INTO route_stations (train_id, station_code, station_name, stop_sequence, distance_from_origin_km, is_source, is_destination)
SELECT 2, 'NGP', 'Nagpur Junction', 3, 1092.0, false, false
WHERE NOT EXISTS (SELECT 1 FROM route_stations WHERE train_id = 2 AND station_code = 'NGP');

INSERT INTO route_stations (train_id, station_code, station_name, stop_sequence, distance_from_origin_km, is_source, is_destination)
SELECT 2, 'NDLS', 'New Delhi', 4, 2182.0, false, true
WHERE NOT EXISTS (SELECT 1 FROM route_stations WHERE train_id = 2 AND station_code = 'NDLS');
