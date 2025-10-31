-- Add active column to Event table
ALTER TABLE Event ADD COLUMN active BOOLEAN DEFAULT TRUE AFTER league_id;

-- Set all existing events to active
UPDATE Event SET active = TRUE WHERE active IS NULL;

