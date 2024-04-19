INSERT INTO roles (name)
VALUES
    ('ROLE_USER'),
    ('ROLE_ADMIN');

INSERT INTO users (username, email, password)
VALUES
    ('user-admin', 'user@gmail.com', '$2a$10$Urd6P/dEZG4KgygviRIaf.RNtgy.vPAhy.A0xxdWYrCHIGmxWqFLa'),
    ('user', 'xxx@gmail.com', '$2a$10$4TxwZzZs4nrVyWdLhjaQku0WIWAQFqpHkj.A1S/LWaF7E1mUHqbBu');

INSERT INTO user_roles (user_id, role_id)
VALUES
    (1,1),
    (1,2),
    (2,1);

INSERT INTO clients (full_name, email, phone_number, status, address, data_of_last_change, data_of_creation, user_id)
VALUES
    ('Piotr Kaczka', 'pitor@gmail.com', '555666777', 'LEAD', 'Poland, Poznań', '2023-12-21', '2023-12-01', 1),
    ('Monika Bałut', 'monika@gmail.com', '000666777', 'LEAD', 'Poland, Warszawa', '2023-12-24', '2023-12-04', 1),
    ('Sara Bernard', 'sara.b@gmail.com', '111699777', 'CLIENT', 'Poland, Poznań', '2023-12-27','2023-12-07', 1),
    ('Marta Czajka', 'marta@gmail.com', '000333777', 'BLACKLIST', 'Poland, Kraków', '2023-12-22','2023-12-02', 1),
    ('Solomon Duda', 'sol@gmail.com', '555666777', 'BLACKLIST', 'Poland, Poznań', '2024-01-11','2024-01-01', 1),
    ('Jonny Depp', 'jonny.b@gmail.com', '456699777', 'CLIENT', 'USA', '2023-11-27','2024-02-07', 1),
    ('Sauron', 'sauron.b@gmail.com', '111000777', 'CLIENT', 'Mordor', '2024-01-27','2023-03-07', 1),
    ('Tacka Jakub', 'tacka@gmail.com', '004874677', 'LEAD', 'Poland, Poznań', '2023-12-26','2023-12-16', 2);

INSERT INTO orders (real_need, estimate_budget, is_project_approved, is_measurements_taken,
                    is_measurement_offered, is_agreement_prepared, is_agreement_signed, was_meeting_in_office,
                    result_price, has_been_paid, address, is_calculation_promised, is_project_shown,
                    is_calculation_shown, data_of_last_change, data_of_creation, client_id)
VALUES
    ('install furniture in the kitchen', 3450.3, true, true, true, true, true, false, 1849.65, true, 'Poland, Poznań', true,
     'SHOWN_ONLINE', 'SHOWN_ONLINE', '2023-12-31', '2023-12-31', 3),
    ('design a bedroom layout', 2001, true, false, true, false, false, true, 0, false, 'Poland, Poznań', true,
     'SHOWN_ONLINE', 'NOT_SHOWN', '2024-01-11', '2023-12-23', 3),
    ('Order a handmade table', 455, false, false, false, false, false, true, 0, false, 'Poland, Poznań', true,
     'NOT_SHOWN', 'NOT_SHOWN', '2024-01-21', '2024-01-11', 3),
    ('install furniture in the bedroom', 4270.2, true, false, true, true, true, false, 1375, false, 'Poland, Kraków', false,
     'SHOWN_IN_OFFICE', 'SHOWN_ONLINE', '2023-12-01', '2023-12-31', 4),
    ('design a bedroom layout', 560, true, false, false, false, false, true, 0, false, 'Poland, Poznań', true,
     'SHOWN_ONLINE', 'NOT_SHOWN', '2024-01-01', '2023-12-13', 1),
    ('install furniture in the kitchen', 1560, true, true, true, false, false, true, 0, false, 'Poland, Poznań', true,
     'SHOWN_ONLINE', 'SHOWN_IN_OFFICE', '2023-12-19', '2023-12-03', 1),
    ('Order a handmade table', 1490.3, true, true, false, true, false, true, 0, false, 'Poland, Warszawa', true,
     'SHOWN_ONLINE', 'NOT_SHOWN', '2024-01-10', '2023-12-31', 2),
    ('PAID ORDER', 1490.3, true, true, false, true, true, true, 0, true, 'Poland, Warszawa', true,
    'SHOWN_ONLINE', 'SHOWN_ONLINE', '2024-02-10', '2023-12-31', 5);

INSERT INTO items_for_additional_purchases (item_name, quantity, unit_price, total_price, order_id)
VALUES
    ('chipboard sheet 400*200', 3, 231.5, 717.65, 2),
    ('chipboard sheet 100*150', 5, 100, 550, 2),
    ('white kitchen countertop 80*300', 1, 450, 495, 2),
    ('door hinge without closer', 10, 12, 132, 2),

    ('chipboard sheet 200*200', 1, 200, 220, 4),
    ('chipboard sheet 100*250', 2, 300, 660, 4),
    ('white kitchen countertop 70*300', 1, 450, 495, 4);

INSERT INTO log_for_user (text, date_of_creation, is_important, is_done, additional_information, tag_name, tag_id, user_id)
VALUES
    ('Installation of kitchen furniture completed', '2023-12-31', true, true, NULL, 'CLIENT', 3, 1),
    ('Bedroom layout design requested', '2023-12-23', true, false, 'Pending approval', 'CLIENT', 3, 1),
    ('Create new user', '2024-01-11', false, false, NULL, 'ADMINISTRATION', 3, 1),
    ('Bedroom furniture installation started', '2023-12-31', true, false, NULL, 'CLIENT', 4, 1),
    ('Design for bedroom layout completed', '2023-12-13', true, false, 'Pending client review', 'CLIENT', 1, 2) ;
