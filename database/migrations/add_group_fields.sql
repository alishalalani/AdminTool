-- Add description and event_group_type_id columns to groups table

-- Add description column if it doesn't exist
ALTER TABLE groups 
ADD COLUMN IF NOT EXISTS description TEXT;

-- Add event_group_type_id column if it doesn't exist
ALTER TABLE groups 
ADD COLUMN IF NOT EXISTS event_group_type_id INT;

-- Add foreign key constraint if it doesn't exist
-- Note: This assumes the event_group_type table already exists
-- If it doesn't exist, you'll need to create it first

-- Check if the foreign key constraint already exists before adding it
-- (MariaDB/MySQL syntax)
SET @constraint_exists = (
    SELECT COUNT(*) 
    FROM information_schema.TABLE_CONSTRAINTS 
    WHERE CONSTRAINT_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'groups' 
    AND CONSTRAINT_NAME = 'fk_groups_event_group_type'
);

SET @sql = IF(@constraint_exists = 0,
    'ALTER TABLE groups ADD CONSTRAINT fk_groups_event_group_type FOREIGN KEY (event_group_type_id) REFERENCES event_group_type(id)',
    'SELECT "Foreign key constraint already exists"'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

