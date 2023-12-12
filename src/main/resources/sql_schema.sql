CREATE DATABASE IF NOT EXISTS marton_db;
USE marton_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS history_messages;
DROP TABLE IF EXISTS order_photos;
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
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    real_need VARCHAR(255) NOT NULL,
    estimate_budget DOUBLE,
    is_project_approved BOOLEAN DEFAULT false,
    was_meeting_in_office BOOLEAN DEFAULT false,
    result_price DOUBLE DEFAULT 0.0,
    has_been_paid BOOLEAN DEFAULT false,
    address VARCHAR(255),
    is_calculation_promised BOOLEAN DEFAULT false,
    is_project_shown VARCHAR(20) DEFAULT 'NOT_SHOWN',
    is_calculation_shown VARCHAR(20) DEFAULT 'NOT_SHOWN',
    data_of_creation DATETIME,
    client_id BIGINT,
    FOREIGN KEY (client_id) REFERENCES clients(client_id)
);

CREATE TABLE history_messages (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text_of_message VARCHAR(255) NOT NULL,
    date_of_creation DATETIME,
    is_important BOOLEAN,
    is_done BOOLEAN,
    note VARCHAR(255),
    client_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (client_id) REFERENCES clients(client_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE item (
    item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    thing VARCHAR(255),
    material VARCHAR(255),
    quantity INT,
    unit_price DOUBLE,
    total_price DOUBLE,
    order_id BIGINT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE order_photos (
    photo_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path VARCHAR(255),
    note VARCHAR(255),
    is_user_photo BOOLEAN,
    order_id BIGINT,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

INSERT INTO roles (name)
VALUES 
('ROLE_USER'), 
('ROLE_MODERATOR'), 
('ROLE_ADMIN');

INSERT INTO users (username, email, password)
VALUES
('igor', 'igor.fizyk@gmail.com', '$2a$10$Urd6P/dEZG4KgygviRIaf.RNtgy.vPAhy.A0xxdWYrCHIGmxWqFLa'),
('xxx', 'xxx@gmail.com', '$2a$10$ZGR8mMOnWNUbvRjJdte13ON5VTxNfx..EfKf7X/wc00oWx4w6cEEi');

INSERT INTO user_roles (user_id, role_id)
VALUES
(1,2),
(1,3),
(2,1);

INSERT INTO clients (full_name, email, phone_number, status, address, user_id)
    VALUES
    ('Piotr Kaczka', 'pitor@gmail.com', '555666777', 'LEAD', 'Poland, Poznań', 1),
    ('Monika Bałut', 'monika@gmail.com', '000666777', 'LEAD', 'Poland, Warszawa', 1),
    ('Sara Bernard', 'sara.b@gmail.com', '111699777', 'LEAD', 'Poland, Poznań, Garbary, 64/3', 1),
    ('Marta Czajka', 'marta@gmail.com', '000333777', 'CLIENT', 'Poland, Poznań', 1),
    ('Solomon Duda', 'sol@gmail.com', '555666777', 'LEAD', 'Poland, Poznań', 1),
    ('Tacka Jakub', 'tacka@gmail.com', '004874677', 'LEAD', 'Poland, Poznań', 2);

INSERT INTO orders (real_need, estimate_budget, address, client_id)
VALUES
    ('install furniture in the kitchen', 3450.3, 'Poland', 3),
    ('design a bedroom layout', 2001, 'Poznań', 3),
    ('Order a handmade table', 5490.3, 'Warszawa', 1);
COMMIT;


SELECT * FROM history_messages;
SELECT * FROM orders;