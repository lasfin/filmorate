# java-filmorate
Template repository for Filmorate project.


## DB structure


### Основные таблицы
-- Таблица пользователей
```
    CREATE TABLE users (
    user_id BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE
);
```
-- Таблица фильмов
```
CREATE TABLE films (
    film_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200) NOT NULL,
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL CHECK (duration > 0)
);
```
-- Таблица рейтингов MPA
```
CREATE TABLE mpa_ratings (
    mpa_rating_id INTEGER PRIMARY KEY,
    name VARCHAR(5) NOT NULL UNIQUE
);
```
-- Таблица жанров
```
CREATE TABLE genres (
    genre_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
```

### Связующие таблицы:
-- Связь фильмов с жанрами (много-ко-многим)
```
CREATE TABLE film_genres (
    film_id BIGINT,
    genre_id INTEGER,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE
);
```

-- Связь фильмов с MPA рейтингом (один-ко-многим)
```
ALTER TABLE films ADD COLUMN mpa_rating_id INTEGER REFERENCES mpa_ratings(mpa_rating_id);
```

-- Таблица лайков фильмов
```
CREATE TABLE film_likes (
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

-- Таблица дружбы
```
CREATE TABLE friendships (
    user_id BIGINT,
    friend_id BIGINT,
    status BOOLEAN DEFAULT false, -- false = неподтвержденная, true = подтвержденная
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

### Начальные данные для справочных таблиц
-- Заполнение таблицы MPA рейтингов
```
INSERT INTO mpa_ratings (mpa_rating_id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');
```
-- Заполнение таблицы жанров
```
INSERT INTO genres (genre_id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');
```
