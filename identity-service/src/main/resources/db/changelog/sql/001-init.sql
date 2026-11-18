--liquibase formatted sql

--changeset denis_akhmin:001-init

CREATE TABLE users (
        id UUID PRIMARY KEY,
        username VARCHAR(255) NOT NULL UNIQUE,
        password_hash VARCHAR(255) NOT NULL,
        role VARCHAR(30) NOT NULL DEFAULT 'USER',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE profiles (
        id UUID PRIMARY KEY,
        user_id UUID NOT NULL UNIQUE,
        first_name VARCHAR(64),
        last_name VARCHAR(64),
        bio TEXT,
        email VARCHAR(128) UNIQUE,
        phone_number VARCHAR(32),
        avatar_url TEXT,

        CONSTRAINT fk_profiles_users
          FOREIGN KEY (user_id)
              REFERENCES users (id)
              ON DELETE CASCADE
);