ALTER TABLE events
ADD COLUMN available_seats INT DEFAULT 0 NOT NULL,
ADD COLUMN event_format VARCHAR(10) CHECK (event_format IN ('ONLINE', 'OFFLINE'));
