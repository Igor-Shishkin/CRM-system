DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS items_for_additional_purchases;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS log_for_user;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                         user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         username VARCHAR(20) NOT NULL,
                         email VARCHAR(50) NOT NULL,
                         user_photo LONGBLOB DEFAULT NULL,
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
                         status VARCHAR(20),
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
                        is_project_shown ENUM ('NOT_SHOWN', 'SHOWN_ONLINE', 'SHOWN_IN_OFFICE') DEFAULT 'NOT_SHOWN',
                        is_calculation_shown ENUM ('NOT_SHOWN', 'SHOWN_ONLINE', 'SHOWN_IN_OFFICE') DEFAULT 'NOT_SHOWN',
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