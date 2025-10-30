-- Verify and Fix Database Tables
-- Run this in HeidiSQL to check if tables exist and create them if needed

USE schedule_tool;

-- Show all existing tables
SHOW TABLES;

-- If tables don't exist or have wrong case, drop them and recreate
DROP TABLE IF EXISTS `Sport`;
DROP TABLE IF EXISTS `sport`;

-- Create Sport table with correct case
CREATE TABLE IF NOT EXISTS `Sport` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    abbreviation VARCHAR(10),
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sport_active (active),
    INDEX idx_sport_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert test data
INSERT INTO `Sport` (name, abbreviation, active) VALUES 
('Football', 'NFL', TRUE),
('Basketball', 'NBA', TRUE),
('Baseball', 'MLB', TRUE),
('Hockey', 'NHL', TRUE);

-- Verify
SELECT * FROM `Sport`;

