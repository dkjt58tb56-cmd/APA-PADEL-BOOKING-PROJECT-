CREATE DATABASE IF NOT EXISTS paddle_court_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE paddle_court_db;

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20)  DEFAULT NULL,
    role        VARCHAR(20)  NOT NULL,
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME     NOT NULL,
    INDEX idx_users_username (username),
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS courts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50)    NOT NULL,
    location        VARCHAR(200)   DEFAULT NULL,
    type            VARCHAR(20)    NOT NULL,
    price_per_hour  DECIMAL(10,2)  NOT NULL,
    description     VARCHAR(500)   DEFAULT NULL,
    available       BOOLEAN        NOT NULL DEFAULT TRUE,
    INDEX idx_courts_available (available)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS bookings (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT        NOT NULL,
    court_id      BIGINT        NOT NULL,
    booking_date  DATE          NOT NULL,
    start_time    TIME          NOT NULL,
    end_time      TIME          NOT NULL,
    total_price   DECIMAL(10,2) NOT NULL,
    status        VARCHAR(20)   NOT NULL,
    notes         VARCHAR(500)  DEFAULT NULL,
    created_at    DATETIME      NOT NULL,
    CONSTRAINT fk_bookings_user  FOREIGN KEY (user_id)  REFERENCES users(id),
    CONSTRAINT fk_bookings_court FOREIGN KEY (court_id) REFERENCES courts(id),
    INDEX idx_bookings_user (user_id),
    INDEX idx_bookings_court_date (court_id, booking_date),
    INDEX idx_bookings_status (status)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS activity_logs (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       DEFAULT NULL,
    action      VARCHAR(50)  NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    ip_address  VARCHAR(50)  DEFAULT NULL,
    created_at  DATETIME     NOT NULL,
    CONSTRAINT fk_logs_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_logs_user (user_id),
    INDEX idx_logs_action (action),
    INDEX idx_logs_created (created_at)
) ENGINE=InnoDB;

