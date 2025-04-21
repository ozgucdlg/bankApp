-- Fix for missing is_read column
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name='notifications' AND column_name='is_read'
    ) THEN
        ALTER TABLE notifications ADD COLUMN is_read BOOLEAN DEFAULT FALSE NOT NULL;
        RAISE NOTICE 'Added is_read column to notifications table';
    ELSE
        RAISE NOTICE 'is_read column already exists';
    END IF;
END $$;

-- Make sure all notifications have is_read value
UPDATE notifications SET is_read = FALSE WHERE is_read IS NULL;

-- Verify table structure
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'notifications'; 