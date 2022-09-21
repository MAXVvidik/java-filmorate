package ru.yandex.practicum.filmorate.storage.film.ratingMpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("ratingMpaDbStorage")
public class RatingMpaDbStorage implements RatingMpaStorage{
    private final JdbcTemplate jdbcTemplate;

    public RatingMpaDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Mpa getMpaRatingById(int id) {
        String sql = "SELECT name FROM ratings WHERE rating_id = ?";
        String ratingStr = jdbcTemplate.queryForObject(sql, String.class, id);
        Mpa mpa = new Mpa();
        mpa.setId(id);
        mpa.setName(ratingStr);
        return mpa;
    }

    @Override
    public List<Mpa> findAllMpaRatings() {
        String sql = "SELECT * FROM ratings ORDER BY rating_id";
        return jdbcTemplate.query(sql, this::mapRowToMPA);
    }

    public Mpa mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("rating_id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    }
}
