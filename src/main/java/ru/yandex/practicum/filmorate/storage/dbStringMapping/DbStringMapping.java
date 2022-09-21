package ru.yandex.practicum.filmorate.storage.dbStringMapping;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Component
public class DbStringMapping {
    private static JdbcTemplate jdbcTemplate;

    public DbStringMapping(JdbcTemplate jdbcTemplate) {
        DbStringMapping.jdbcTemplate = jdbcTemplate;
    }

    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        String sqlForMpa = "SELECT * FROM ratings WHERE rating_id = ?";
        Film film = Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(Objects.requireNonNull(resultSet.getDate("releaseDate")).toLocalDate())
                .mpa(jdbcTemplate.queryForObject(sqlForMpa, DbStringMapping::mapRowToMPA, resultSet.getInt("rating_id")))
                .rate(resultSet.getInt("rate"))
                .build();
        String sqlForGenres = "select genres.category_id, genres.name from GENRES left join FILM_GENRES FG on " +
                "GENRES.CATEGORY_ID = FG.CATEGORY_ID WHERE FG.FILM_ID = ?";
        List<Genre> genres = jdbcTemplate.query(sqlForGenres, DbStringMapping::mapRowToGenre, film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
        return film;
    }
    public static Mpa mapRowToMPA(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("rating_id"));
        mpa.setName(resultSet.getString("name"));
        return mpa;
    }
    public static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genres.category_id"));
        genre.setName(resultSet.getString("genres.name"));
        return genre;
    }
}
