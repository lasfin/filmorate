package ru.yandex.practicum.filmorate.repository.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class H2MpaRepo implements MpaRepo {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public H2MpaRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_rating_id";
        return jdbcTemplate.query(sql, new MpaRowMapper());
    }

    @Override
    public Optional<MpaRating> getMpaById(Integer id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_rating_id = ?";
        List<MpaRating> mpaList = jdbcTemplate.query(sql, new MpaRowMapper(), id);
        if (mpaList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mpaList.get(0));
    }

    private static class MpaRowMapper implements RowMapper<MpaRating> {
        @Override
        public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
            MpaRating mpa = new MpaRating();
            mpa.setId(rs.getLong("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        }
    }
}