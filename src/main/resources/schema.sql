CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL
);

CREATE TABLE events (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        date DATE NOT NULL,
                        max_participants INT NOT NULL,
                        status VARCHAR(50) NOT NULL
);

CREATE TABLE tickets (
                         id SERIAL PRIMARY KEY,
                         ticket_number UUID NOT NULL UNIQUE,
                         user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                         event_id BIGINT REFERENCES events(id) ON DELETE CASCADE,
                         status VARCHAR(50) NOT NULL
);
