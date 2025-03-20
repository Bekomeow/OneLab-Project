CREATE TABLE registrations (
                               id SERIAL PRIMARY KEY,
                               username VARCHAR(50) NOT NULL,
                               event_id INT NOT NULL,
                               registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               UNIQUE (username, event_id),
                               CONSTRAINT fk_registrations_events FOREIGN KEY (event_id) REFERENCES events(id)
);
