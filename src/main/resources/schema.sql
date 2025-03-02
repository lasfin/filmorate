-- Movie Social Network Database Schema

-- Create mpa_ratings table
CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_rating_id INT PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE
);

-- Create genres table
CREATE TABLE IF NOT EXISTS genres (
    genre_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    birthday DATE NOT NULL
);

-- Create films table
CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE,
    duration INT CHECK (duration > 0),
    mpa_rating_id INT,
    FOREIGN KEY (mpa_rating_id) REFERENCES mpa_ratings(mpa_rating_id)
);

-- Create film_genres junction table
CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE
);

-- Create film_likes junction table
CREATE TABLE IF NOT EXISTS film_likes (
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create friendships table
CREATE TABLE IF NOT EXISTS friendships (
    user_id BIGINT,
    friend_id BIGINT,
    status BOOLEAN NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CHECK (user_id != friend_id) -- Prevent self-friendship
);

MERGE INTO mpa_ratings (mpa_rating_id, name) VALUES (1, 'G');
MERGE INTO mpa_ratings (mpa_rating_id, name) VALUES (2, 'PG');
MERGE INTO mpa_ratings (mpa_rating_id, name) VALUES (3, 'PG-13');
MERGE INTO mpa_ratings (mpa_rating_id, name) VALUES (4, 'R');
MERGE INTO mpa_ratings (mpa_rating_id, name) VALUES (5, 'NC-17');

MERGE INTO genres (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, name) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, name) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, name) VALUES (6, 'Боевик');