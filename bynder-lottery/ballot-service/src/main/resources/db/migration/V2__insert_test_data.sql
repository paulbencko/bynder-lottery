INSERT INTO ballots (participant_id, lottery_id, lottery_type, lottery_name, numbers, created_at, prize) VALUES
(1, 3, 'EURO_DREAMS', 'Euro Dreams', ARRAY[7,8,9,10,11,12], CURRENT_TIMESTAMP - INTERVAL '2 days', 'NO_PRIZE'),
(1, 2, 'EL_GORDO', 'El Gordo', ARRAY[1,2,3,9,10], CURRENT_TIMESTAMP - INTERVAL '2 days', 'THIRD_PRIZE'),
(1, 1, 'EURO_MILLONES', 'Euro Millones', ARRAY[1,7,8,9,10], CURRENT_TIMESTAMP - INTERVAL '2 days', 'NO_PRIZE'),
(1, 1, 'EURO_MILLONES', 'Euro Millones', ARRAY[1,2,3,4,5], CURRENT_TIMESTAMP - INTERVAL '3 days', 'FIRST_PRIZE');
