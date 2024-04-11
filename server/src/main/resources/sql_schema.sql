CREATE DATABASE IF NOT EXISTS marton_db;
USE marton_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS items_for_additional_purchases;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS history_messages;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

START TRANSACTION;

CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(20) NOT NULL,
                       email VARCHAR(50) NOT NULL,
                       user_photo LONGBLOB,
                       password VARCHAR(120) NOT NULL
);

CREATE TABLE roles (
                       role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(20) NOT NULL
);

CREATE TABLE user_roles (
                            user_id BIGINT,
                            role_id BIGINT,
                            FOREIGN KEY (user_id) REFERENCES users(user_id),
                            FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE clients (
                         client_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         full_name VARCHAR(255) NOT NULL,
                         email VARCHAR(80) UNIQUE NOT NULL,
                         phone_number VARCHAR(50),
                         status VARCHAR(20) DEFAULT 'LEAD',
                         address VARCHAR(255),
                         data_of_last_change DATETIME,
                         data_of_creation DATETIME,
                         user_id BIGINT,
                         FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE orders (
                        order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        real_need VARCHAR(255) NOT NULL,
                        estimate_budget DOUBLE,
                        is_project_approved BOOLEAN DEFAULT false,
                        is_measurements_taken BOOLEAN DEFAULT false,
                        is_measurement_offered BOOLEAN DEFAULT false,
                        is_agreement_prepared BOOLEAN DEFAULT false,
                        is_agreement_signed BOOLEAN DEFAULT false,
                        was_meeting_in_office BOOLEAN DEFAULT false,
                        result_price DOUBLE DEFAULT 0.0,
                        has_been_paid BOOLEAN DEFAULT false,
                        address TEXT,
                        is_calculation_promised BOOLEAN DEFAULT false,
                        is_project_shown VARCHAR(20) DEFAULT 'NOT_SHOWN',
                        is_calculation_shown VARCHAR(20) DEFAULT 'NOT_SHOWN',
                        data_of_last_change DATETIME,
                        data_of_creation DATETIME,
                        client_id BIGINT,
                        FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

CREATE TABLE log_for_user (
                        entry_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        text VARCHAR(255) NOT NULL,
                        date_of_creation DATETIME,
                        deadline DATETIME,
                        is_important BOOLEAN,
                        is_done BOOLEAN,
                        additional_information TEXT,
                        tag_name VARCHAR(30),
                        tag_id BIGINT,
                        user_id BIGINT,
                        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE items_for_additional_purchases (
                       item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       item_name TEXT,
                       quantity INT,
                       unit_price DOUBLE,
                       total_price DOUBLE,
                       order_id BIGINT,
                       FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

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
    ('Marta Czajka', 'marta@gmail.com', '000333777', 'CLIENT', 'Poland, Kraków', '2023-12-22','2023-12-02', 1),
    ('Solomon Duda', 'sol@gmail.com', '555666777', 'LEAD', 'Poland, Poznań', '2024-01-11','2024-01-01', 1),
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
    ('install furniture in the bedroom', 4270.2, true, false, true, true, true, false, 1375, true, 'Poland, Kraków', false,
     'SHOWN_IN_OFFICE', 'SHOWN_ONLINE', '2023-12-01', '2023-12-31', 4),
    ('design a bedroom layout', 560, true, false, false, false, false, true, 0, false, 'Poland, Poznań', true,
     'SHOWN_ONLINE', 'NOT_SHOWN', '2024-01-01', '2023-12-13', 1),
    ('install furniture in the kitchen', 1560, true, true, true, false, false, true, 0, false, 'Poland, Poznań', true,
     'SHOWN_ONLINE', 'SHOWN_IN_OFFICE', '2023-12-19', '2023-12-03', 1),
    ('Order a handmade table', 1490.3, true, true, false, true, false, true, 0, false, 'Poland, Warszawa', true,
     'SHOWN_ONLINE', 'NOT_SHOWN', '2024-01-10', '2023-12-31', 2);

INSERT INTO items_for_additional_purchases (item_name, quantity, unit_price, total_price, order_id)
VALUES
    ('chipboard sheet 400*200', 3, 231.5, 717.65, 1),
    ('chipboard sheet 100*150', 5, 100, 550, 1),
    ('white kitchen countertop 80*300', 1, 450, 495, 1),
    ('door hinge without closer', 10, 12, 132, 1),

    ('chipboard sheet 200*200', 1, 200, 220, 4),
    ('chipboard sheet 100*250', 2, 300, 660, 4),
    ('white kitchen countertop 70*300', 1, 450, 495, 4);

INSERT INTO log_for_user(text, date_of_creation, is_important, is_done, additional_information, tag_name, tag_id, user_id)
VALUES
('Installation of kitchen furniture completed', '2023-12-31', true, true, NULL, 'CLIENT', 3, 1),
('Bedroom layout design requested', '2023-12-23', true, false, 'Pending approval', 'CLIENT', 3, 1),
('Handmade table order placed', '2024-01-11', false, false, NULL, 'CLIENT', 3, 1),
('Bedroom furniture installation started', '2023-12-31', true, false, NULL, 'CLIENT', 4, 1),
('Design for bedroom layout completed', '2023-12-13', true, false, 'Pending client review', 'CLIENT', 1, 1),
('Kitchen furniture installation scheduled', '2023-12-03', true, true, 'Client confirmed', 'CLIENT', 1, 1),
('Handmade table order delivered', '2023-12-31', true, true, 'Client satisfied', 'CLIENT', 2, 1),
('Bedroom furniture installation completed', '2024-01-02', true, true, NULL, 'CLIENT', 3, 1),
('Kitchen furniture installation in progress', '2024-01-05', true, false, NULL, 'CLIENT', 3, 1),
('Handmade table order canceled', '2024-01-12', false, true, 'Client changed mind', 'CLIENT', 3, 1),
('Bedroom layout design approved', '2024-01-15', true, true, 'Ready for implementation', 'CLIENT', 3, 1),
('Kitchen furniture installation delayed', '2024-01-20', true, false, 'Waiting for materials', 'CLIENT', 4, 1),
('Handmade table order refunded', '2024-01-25', true, true, 'Refund processed', 'CLIENT', 1, 1),
('Kitchen furniture installation completed', '2024-01-28', true, true, 'Client satisfied', 'CLIENT', 1, 1),
('Bedroom layout design rejected', '2024-01-02', true, true, 'Client not satisfied', 'CLIENT', 2, 1),
('Handmade table order rescheduled', '2024-01-11', true, false, 'New delivery date set', 'CLIENT', 2, 1),
('Kitchen furniture installation rescheduled', '2024-02-10', true, false, 'Weather conditions', 'CLIENT', 3, 1),
('Bedroom furniture installation delayed', '2024-01-22', true, false, 'Supplier issue', 'CLIENT', 2, 1),
('Handmade table order out of stock', '2024-02-20', true, false, 'Awaiting restock', 'CLIENT', 2, 1),
('Bedroom layout design revision requested', '2024-01-12', true, false, 'Client feedback', 'CLIENT', 1, 1),
('Kitchen furniture installation inspection', '2024-01-15', true, false, 'Final check', 'CLIENT', 1, 1),
('Handmade table order returned', '2024-03-05', true, true, 'Quality issue', 'CLIENT', 1, 1),
('Bedroom furniture installation troubleshooting', '2024-01-12', true, false, 'Technical problem', 'CLIENT', 1, 1),
('Kitchen furniture installation warranty claim', '2024-01-15', true, false, 'Damage reported', 'CLIENT', 3, 1),
('Handmade table order exchange requested', '2024-01-20', true, false, 'Size mismatch', 'CLIENT', 1, 1),
('Bedroom layout design completion confirmation', '2024-01-25', true, true, 'Client approved', 'CLIENT', 1, 1),
('Kitchen furniture installation review', '2024-01-30', true, false, 'Client feedback', 'CLIENT', 3, 1),
('Handmade table order modification', '2024-01-05', true, false, 'Change in specifications', 'CLIENT', 1, 1),
('Bedroom furniture installation completion', '2024-01-10', true, true, 'Project finished', 'CLIENT', 1, 1),
('Kitchen furniture installation feedback received', '2024-01-15', true, true, 'Client satisfied', 'CLIENT', 1, 1),
('Some admin deal', '2024-01-31', true, true, 'Client satisfied', 'ADMINISTRATION', 1, 1),
('Add new user', '2023-12-29', true, true, 'Hej-hO', 'ADMINISTRATION', 1, 1),
('Message without tagName', '2024-01-30', true, true, 'Hej-hO', 'EMPTY', 0, 1),
('Message for second user', '2024-01-30', true, true, 'Hej-hO', 'CLIENT', 0, 2) ;


COMMIT;
