-- Additional trains for connecting journeys
INSERT INTO trains (train_number, train_name, train_type, active_status)
SELECT '12627', 'Karnataka Express', 'SUPERFAST', true
WHERE NOT EXISTS (SELECT 1 FROM trains WHERE train_number = '12627');
