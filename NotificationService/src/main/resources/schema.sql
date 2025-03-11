CREATE TABLE notification_log (
                                  id SERIAL PRIMARY KEY,
                                  recipient_email VARCHAR(255) NOT NULL,
                                  subject TEXT NOT NULL,
                                  message TEXT NOT NULL,
                                  status VARCHAR(10) NOT NULL CHECK (status IN ('SENT', 'FAILED')),
                                  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
