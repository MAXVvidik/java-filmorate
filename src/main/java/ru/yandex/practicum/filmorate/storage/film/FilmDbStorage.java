package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InputDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dbStringMapping.DbStringMapping;


import java.sql.PreparedStatement;
import java.util.List;
@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }
    @Override
    public List<Film> findAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, DbStringMapping::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films(name, description, releaseDate, duration, rate, rating_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        int idFilm = keyHolder.getKey().intValue();
        if(!(film.getGenres() == null)) {
            String sqlForGenre = "INSERT INTO film_genres(film_id, category_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenre, idFilm, genre.getId());
            }
        }
        film.setId(idFilm);
        return film;
    }

    @Override
    public void updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, releasedate = ?, duration = ?, rating_id = ?" +
                ", rate = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getRate(), film.getId());
        if(film.getGenres() == null) {
            String sqlDeleteGenres = "DELETE FROM film_genres WHERE film_id = ?";
            jdbcTemplate.update(sqlDeleteGenres, film.getId());
        } else {
            String sqlDeleteGenres = "DELETE FROM film_genres WHERE film_id = ?";
            jdbcTemplate.update(sqlDeleteGenres, film.getId());
            String sqlForGenre = "INSERT INTO film_genres(film_id, category_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlForGenre, film.getId(), genre.getId());
            }
        }

    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, DbStringMapping::mapRowToFilm, id);
    }

    @Override
    public boolean isContainsFilms(int id) {
        String sql = "SELECT film_id FROM films";
        List<Integer> queryForColumn = jdbcTemplate.queryForList(sql, Integer.class);
        return queryForColumn.contains(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO amountlikes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sqlCheck = "SELECT EXISTS (SELECT * FROM amountlikes WHERE film_id = ? AND user_id = ?)";
        boolean result = jdbcTemplate.queryForObject(sqlCheck, Boolean.class, filmId, userId);
        if(result) {
            String sqlDelete = "DELETE FROM amountlikes WHERE user_id = ?";
            jdbcTemplate.update(sqlDelete, userId);
        } else {
            log.warn("Невозможно удалить лайк - лайк не найден");
            throw new InputDataException("Лайк для удаление не найден");
        }
    }
    @Override
    public List<Film> getPopularFilms(String count) {
        String sql = "SELECT f.* FROM films AS f LEFT JOIN amountlikes AS al ON f.film_id=al.film_id " +
                "GROUP BY f.film_id ORDER BY COUNT(al.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, DbStringMapping::mapRowToFilm, count);
    }
}
