CREATE TABLE Attributes
(
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);
INSERT INTO Attributes(name)
VALUES ('tag');
INSERT INTO Attributes(name)
VALUES ('vk_id');
INSERT INTO Attributes(name)
VALUES ('access_token');
INSERT INTO Attributes(name)
VALUES ('last_sync_timestamp');

CREATE TYPE LOCATION AS
(
    latitude  DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);

CREATE TYPE USER_ROLE AS ENUM ('admin', 'member');

CREATE TABLE Cities
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location LOCATION
);

CREATE TABLE Tags
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Users_group
(
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE Users
(
    id          SERIAL PRIMARY KEY,
    group_id    INT         REFERENCES Users_group (id) ON DELETE SET NULL,
    username    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(75) NOT NULL UNIQUE,
    password    VARCHAR(50) NOT NULL,
    city_id     INT         REFERENCES Cities (id) ON DELETE SET NULL NOT NULL,
    gender      VARCHAR(1),
    description VARCHAR(100),
    avatar_path TEXT
);

CREATE TABLE Users_attribute_value
(
    id           SERIAL PRIMARY KEY,
    user_id      INT REFERENCES Users (id) ON DELETE CASCADE,
    attribute_id INT REFERENCES Attributes (id) ON DELETE CASCADE,
    value        TEXT NOT NULL
);

CREATE TABLE Events
(
    id            SERIAL PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    description   VARCHAR(1000),
    price         DECIMAL(10, 2),
    date          TIMESTAMP,
    location      LOCATION,
    cover_img_url TEXT,
    source_url    TEXT
);

CREATE TABLE Events_attribute_value
(
    id           SERIAL PRIMARY KEY,
    event_id     INT REFERENCES Events (id) ON DELETE CASCADE,
    attribute_id INT REFERENCES Attributes (id) ON DELETE CASCADE,
    value        TEXT NOT NULL
);

CREATE TABLE Users_event
(
    user_id     INT REFERENCES Users (id) ON DELETE CASCADE,
    event_id    INT REFERENCES Events (id) ON DELETE CASCADE,
    is_liked    BOOLEAN DEFAULT FALSE,
    is_disliked BOOLEAN DEFAULT FALSE,
    in_calendar BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, event_id)
);

CREATE TABLE Chats
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(100),
    is_group BOOLEAN DEFAULT FALSE
);

CREATE TABLE Chats_members
(
    chat_id INT REFERENCES Chats (id) ON DELETE CASCADE,
    user_id INT REFERENCES Users (id) ON DELETE CASCADE,
    role    USER_ROLE NOT NULL,
    PRIMARY KEY (chat_id, user_id)
);

CREATE TABLE Messages
(
    id        SERIAL PRIMARY KEY,
    chat_id   INT REFERENCES Chats (id) ON DELETE CASCADE,
    user_id   INT  REFERENCES Users (id) ON DELETE SET NULL,
    content   TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);