-- Schedule Tool v5.0 - MariaDB Schema
-- Migration from MSSQL to MariaDB

-- Create database
CREATE DATABASE IF NOT EXISTS schedule_tool 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE schedule_tool;

-- ============================================================================
-- CORE TABLES
-- ============================================================================

-- Sport table
CREATE TABLE IF NOT EXISTS Sport (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    abbreviation VARCHAR(10),
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sport_active (active),
    INDEX idx_sport_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- League table
CREATE TABLE IF NOT EXISTS League (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sport_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sport_id) REFERENCES Sport(id),
    INDEX idx_league_sport (sport_id),
    INDEX idx_league_active (active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Location table
CREATE TABLE IF NOT EXISTS Location (
    id INT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(100),
    state VARCHAR(50),
    country VARCHAR(50),
    timezone VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Venue table
CREATE TABLE IF NOT EXISTS Venue (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location_id INT,
    capacity INT,
    surface VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (location_id) REFERENCES Location(id),
    INDEX idx_venue_location (location_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Team table (generic teams)
CREATE TABLE IF NOT EXISTS Team (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(10),
    city VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- League_Team table (teams in specific leagues)
CREATE TABLE IF NOT EXISTS League_Team (
    id INT AUTO_INCREMENT PRIMARY KEY,
    league_id INT NOT NULL,
    team_id INT,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(10),
    rotation_number INT,
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (league_id) REFERENCES League(id),
    FOREIGN KEY (team_id) REFERENCES Team(id),
    INDEX idx_league_team_league (league_id),
    INDEX idx_league_team_team (team_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Player table
CREATE TABLE IF NOT EXISTS Player (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    full_name VARCHAR(200),
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_player_name (last_name, first_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- League_Position table (positions in a league, e.g., QB, RB, etc.)
CREATE TABLE IF NOT EXISTS League_Position (
    id INT AUTO_INCREMENT PRIMARY KEY,
    league_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    abbreviation VARCHAR(10),
    sequence INT,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (league_id) REFERENCES League(id),
    INDEX idx_league_position_league (league_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- League_Player table
CREATE TABLE IF NOT EXISTS League_Player (
    id INT AUTO_INCREMENT PRIMARY KEY,
    league_id INT NOT NULL,
    player_id INT NOT NULL,
    jersey_number INT,
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (league_id) REFERENCES League(id),
    FOREIGN KEY (player_id) REFERENCES Player(id),
    INDEX idx_league_player_league (league_id),
    INDEX idx_league_player_player (player_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- League_Team_Player table
CREATE TABLE IF NOT EXISTS League_Team_Player (
    id INT AUTO_INCREMENT PRIMARY KEY,
    league_team_id INT NOT NULL,
    league_player_id INT NOT NULL,
    league_position_id INT,
    jersey_number INT,
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (league_team_id) REFERENCES League_Team(id),
    FOREIGN KEY (league_player_id) REFERENCES League_Player(id),
    FOREIGN KEY (league_position_id) REFERENCES League_Position(id),
    INDEX idx_ltp_team (league_team_id),
    INDEX idx_ltp_player (league_player_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- SCHEDULE & EVENT TABLES
-- ============================================================================

-- Schedule table
CREATE TABLE IF NOT EXISTS Schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_schedule_date (date),
    INDEX idx_schedule_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Category_Type table
CREATE TABLE IF NOT EXISTS Category_Type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Category table
CREATE TABLE IF NOT EXISTS Category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    league_id INT NOT NULL,
    category_type_id INT,
    date DATE NOT NULL,
    end_date DATE,
    header TEXT,
    exclude BOOLEAN DEFAULT FALSE,
    override BOOLEAN DEFAULT FALSE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (league_id) REFERENCES League(id),
    FOREIGN KEY (category_type_id) REFERENCES Category_Type(id),
    INDEX idx_category_league (league_id),
    INDEX idx_category_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Schedule_Category junction table
CREATE TABLE IF NOT EXISTS Schedule_Category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    schedule_id INT NOT NULL,
    category_id INT NOT NULL,
    sequence INT,
    FOREIGN KEY (schedule_id) REFERENCES Schedule(id),
    FOREIGN KEY (category_id) REFERENCES Category(id),
    INDEX idx_sc_schedule (schedule_id),
    INDEX idx_sc_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event table
CREATE TABLE IF NOT EXISTS Event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    number INT NOT NULL,
    league_id INT NOT NULL,
    exclude BOOLEAN DEFAULT FALSE,
    double_header INT DEFAULT 0,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (league_id) REFERENCES League(id),
    UNIQUE KEY unique_event (date, number),
    INDEX idx_event_date (date),
    INDEX idx_event_league (league_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Source table (data sources)
CREATE TABLE IF NOT EXISTS Source (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event_Time table
CREATE TABLE IF NOT EXISTS Event_Time (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    time TIMESTAMP,
    TBA BOOLEAN DEFAULT FALSE,
    override BOOLEAN DEFAULT FALSE,
    source_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES Event(id),
    FOREIGN KEY (source_id) REFERENCES Source(id),
    INDEX idx_event_time_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event_Venue table
CREATE TABLE IF NOT EXISTS Event_Venue (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    venue_id INT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES Event(id),
    FOREIGN KEY (venue_id) REFERENCES Venue(id),
    INDEX idx_event_venue_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event_Item table (participants in an event)
CREATE TABLE IF NOT EXISTS Event_Item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    sequence INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES Event(id),
    INDEX idx_event_item_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event_Item_League_Team table
CREATE TABLE IF NOT EXISTS Event_Item_League_Team (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_item_id INT NOT NULL,
    league_team_id INT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (event_item_id) REFERENCES Event_Item(id),
    FOREIGN KEY (league_team_id) REFERENCES League_Team(id),
    INDEX idx_eilt_event_item (event_item_id),
    INDEX idx_eilt_league_team (league_team_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- SCORING TABLES
-- ============================================================================

-- Event_Score table
CREATE TABLE IF NOT EXISTS Event_Score (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    source_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES Event(id),
    FOREIGN KEY (source_id) REFERENCES Source(id),
    INDEX idx_event_score_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event_Score_Item table
CREATE TABLE IF NOT EXISTS Event_Score_Item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_score_id INT NOT NULL,
    event_item_id INT NOT NULL,
    score INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_score_id) REFERENCES Event_Score(id),
    FOREIGN KEY (event_item_id) REFERENCES Event_Item(id),
    INDEX idx_esi_event_score (event_score_id),
    INDEX idx_esi_event_item (event_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- SPORTSBOOK & ODDS TABLES
-- ============================================================================

-- Sportsbook table
CREATE TABLE IF NOT EXISTS Sportsbook (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(20),
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Lineserver_Sportsbook table (odds sportsbooks)
CREATE TABLE IF NOT EXISTS Lineserver_Sportsbook (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sportsbook_id INT NOT NULL,
    name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (sportsbook_id) REFERENCES Sportsbook(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- UTILITY TABLES
-- ============================================================================

-- Preset_Message table
CREATE TABLE IF NOT EXISTS Preset_Message (
    id INT AUTO_INCREMENT PRIMARY KEY,
    value TEXT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- League_Equivalent table (mapping between different league representations)
CREATE TABLE IF NOT EXISTS League_Equivalent (
    id INT AUTO_INCREMENT PRIMARY KEY,
    league_id INT NOT NULL,
    external_id VARCHAR(50),
    source VARCHAR(50),
    FOREIGN KEY (league_id) REFERENCES League(id),
    INDEX idx_league_equiv_league (league_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- SAMPLE DATA
-- ============================================================================

-- Insert sample sports
INSERT INTO Sport (name, abbreviation, active) VALUES
('Basketball', 'NBA', TRUE),
('Football', 'NFL', TRUE),
('Baseball', 'MLB', TRUE),
('Hockey', 'NHL', TRUE),
('Soccer', 'MLS', TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- Insert sample sources
INSERT INTO Source (name, active) VALUES
('Manual', TRUE),
('OddsLogic', TRUE),
('API', TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- Insert sample category types
INSERT INTO Category_Type (name, description) VALUES
('Regular', 'Regular season games'),
('Playoff', 'Playoff games'),
('Championship', 'Championship games'),
('Preseason', 'Preseason games')
ON DUPLICATE KEY UPDATE name=name;

