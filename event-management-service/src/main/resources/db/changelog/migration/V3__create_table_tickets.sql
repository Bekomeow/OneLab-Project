CREATE TABLE tickets (
                         id SERIAL PRIMARY KEY,
                         username VARCHAR(50) NOT NULL,
                         event_id INT NOT NULL,
                         ticket_code UUID DEFAULT gen_random_uuid(),
                         status VARCHAR(20) CHECK (status IN ('ACTIVE', 'USED', 'CANCELLED')),
                         CONSTRAINT fk_tickets_events FOREIGN KEY (event_id) REFERENCES events(id),
                         UNIQUE (username, event_id)
);
