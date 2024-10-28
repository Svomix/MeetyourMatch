CREATE TABLE IF NOT EXISTS tags
(
    tag_id   SERIAL PRIMARY KEY,
    tag_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    event_id        SERIAL PRIMARY KEY,
    title           VARCHAR(100) NOT NULL,
    description     VARCHAR(1000),
    date_and_time   TIMESTAMP    NOT NULL,
    location        DECIMAL[2]   NOT NULL,
    cover_image_url TEXT,
    source_url      TEXT
);

-- Таблица-связка для связи между событиями и тегами
CREATE TABLE IF NOT EXISTS event_tags
(
    event_id INT REFERENCES events(event_id) ON DELETE CASCADE,
    tag_id   INT REFERENCES tags(tag_id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, tag_id)
);

CREATE TABLE IF NOT EXISTS cities
(
    city_id   SERIAL PRIMARY KEY,
    city_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_data
(
    user_id         SERIAL PRIMARY KEY,
    username        VARCHAR(50) NOT NULL,
    email           VARCHAR(50) NOT NULL UNIQUE,
    avatar_path     VARCHAR(255),
    city            INT REFERENCES cities(city_id) ON DELETE CASCADE,
    age             INT,
    sex             VARCHAR(1),
    password        VARCHAR(50) NOT NULL
);

-- Таблица-связка для связи интересов пользователя с тегами
CREATE TABLE IF NOT EXISTS user_interests
(
    user_id INT REFERENCES user_data(user_id) ON DELETE CASCADE,
    tag_id  INT REFERENCES tags(tag_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, tag_id)
);

-- Таблица-связка для связи понравившихся событий пользователя
CREATE TABLE IF NOT EXISTS user_liked_events
(
    user_id  INT REFERENCES user_data(user_id) ON DELETE CASCADE,
    event_id INT REFERENCES events(event_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, event_id)
);

-- Таблица-связка для связи событий, добавленных пользователем в календарь
CREATE TABLE IF NOT EXISTS user_calendar_events
(
    user_id  INT REFERENCES user_data(user_id) ON DELETE CASCADE,
    event_id INT REFERENCES events(event_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, event_id)
);