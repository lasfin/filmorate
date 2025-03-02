package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Primary
@Component
public class H2GenreRepo implements GenreRepo {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public H2GenreRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbcTemplate.query(sql, new GenreRowMapper());
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new GenreRowMapper(), id);
        if (genres.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(genres.get(0));
    }

    public List<Genre> getGenresByFilmId(Long filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, new GenreRowMapper(), filmId);
    }

    public void addGenresToFilm(Long filmId, List<Genre> genres) {
        String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);

        if (genres != null && !genres.isEmpty()) {
            String insertSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : genres) {
                jdbcTemplate.update(insertSql, filmId, genre.getId());
            }
        }
    }

    private static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }
    }
}