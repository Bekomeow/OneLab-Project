CREATE TABLE events (
                        id SERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        description TEXT,
                        start_date TIMESTAMP NOT NULL,
                        end_date TIMESTAMP NOT NULL,
                        location VARCHAR(255),
                        max_participants INT NOT NULL,
                        status VARCHAR(20) CHECK (status IN ('DRAFT', 'PUBLISHED', 'REGISTRATION_CLOSED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
                        organizer_name VARCHAR(50) NOT NULL
);

