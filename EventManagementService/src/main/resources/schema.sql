CREATE TABLE events (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        description TEXT,
                        date TIMESTAMP NOT NULL,
                        location VARCHAR(255),
                        max_participants INT NOT NULL,
                        status VARCHAR(20) CHECK (status IN ('DRAFT', 'PUBLISHED', 'REGISTRATION_CLOSED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
                        organizer_id INT NOT NULL
);

CREATE TABLE registrations (
                               id SERIAL PRIMARY KEY,
                               user_id INT NOT NULL,
                               event_id INT NOT NULL,
                               registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               UNIQUE (user_id, event_id),
                               CONSTRAINT fk_registrations_events FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE tickets (
                         id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL,
                         event_id INT NOT NULL,
                         ticket_code UUID DEFAULT gen_random_uuid(),
                         status VARCHAR(20) CHECK (status IN ('ACTIVE', 'USED', 'CANCELLED')),
                         CONSTRAINT fk_tickets_events FOREIGN KEY (event_id) REFERENCES events(id),
                         UNIQUE (user_id, event_id)
);
