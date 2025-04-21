-- Check if is_read column exists, add it if it doesn't
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name='notifications' AND column_name='is_read'
    ) THEN
        -- Add is_read column to notifications table
        ALTER TABLE notifications ADD COLUMN is_read BOOLEAN DEFAULT FALSE NOT NULL;
        
        -- Update existing records to ensure they're kept as unread
        UPDATE notifications SET is_read = FALSE WHERE is_read IS NULL;
    END IF;
END $$; 