CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (name) VALUES
                             ('ADMIN'),
                             ('MODERATOR'),
                             ('USER');


CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role_id INT NOT NULL,
                       CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES roles(id)
);
