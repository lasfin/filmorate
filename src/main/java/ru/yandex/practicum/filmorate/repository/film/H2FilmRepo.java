package ru.yandex.practicum.filmorate.repository.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Primary
@Repository
public class H2FilmRepo implements FilmRepo {

    private final JdbcTemplate jdbcTemplate;

    public H2FilmRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    new String[]{"film_id"}
            );
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);

        // Save film genres if present
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveFilmGenres(film);
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE film_id = ?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        // Delete existing genres and save new ones
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveFilmGenres(film);
        }

        return getFilm(film.getId());
    }

    @Override
    public Film deleteFilm(Film film) {
        // Delete references in film_genres table
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        // Delete references in film_likes table
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ?", film.getId());

        // Delete the film
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", film.getId());

        return film;
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query(
                "SELECT f.*, m.mpa_rating_id, m.name as mpa_name " +
                        "FROM films f " +
                        "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id",
                this::mapRowToFilm
        );
    }

    @Override
    public Film getFilm(Long filmId) {
        Film film = jdbcTemplate.queryForObject(
                "SELECT f.*, m.mpa_rating_id, m.name as mpa_name " +
                        "FROM films f " +
                        "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id " +
                        "WHERE f.film_id = ?",
                this::mapRowToFilm,
                filmId
        );

        // Load genres for the film
        if (film != null) {
            loadFilmGenres(film);
            loadFilmLikes(film);
        }

        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        // First check if the like already exists
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?",
                Integer.class,
                filmId, userId
        );

        // Only insert if it doesn't exist
        if (count == 0) {
            jdbcTemplate.update(
                    "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)",
                    filmId, userId
            );
        }

        return getFilm(filmId);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(
                "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?",
                filmId, userId
        );

        return getFilm(filmId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return jdbcTemplate.query(
                "SELECT f.*, m.mpa_rating_id, m.name as mpa_name, COUNT(fl.user_id) as likes_count " +
                        "FROM films f " +
                        "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id " +
                        "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                        "GROUP BY f.film_id " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?",
                this::mapRowToFilm,
                count
        );
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        MpaRating mpa = new MpaRating();
        mpa.setId(rs.getLong("mpa_rating_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        // Genres and likes will be loaded separately
        film.setGenres(new HashSet<>());
        film.setLikes(new HashSet<>());

        return film;
    }

    private void saveFilmGenres(Film film) {
        // First delete existing genres to avoid duplicates
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        // Then insert the new genres
        List<Object[]> batchArgs = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            batchArgs.add(new Object[]{film.getId(), genre.getId()});
        }

        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                    batchArgs
            );
        }
    }

    private void loadFilmGenres(Film film) {
        List<Genre> genres = jdbcTemplate.query(
                "SELECT g.genre_id, g.name " +
                        "FROM film_genres fg " +
                        "JOIN genres g ON fg.genre_id = g.genre_id " +
                        "WHERE fg.film_id = ?",
                (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("genre_id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                },
                film.getId()
        );

        film.setGenres(new HashSet<>(genres));
    }

    private void loadFilmLikes(Film film) {
        List<Long> likes = jdbcTemplate.query(
                "SELECT user_id FROM film_likes WHERE film_id = ?",
                (rs, rowNum) -> rs.getLong("user_id"),
                film.getId()
        );

        film.setLikes(new HashSet<>(likes));
    }
}