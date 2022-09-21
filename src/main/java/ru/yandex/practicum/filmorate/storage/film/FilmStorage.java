package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
public interface FilmStorage {
    List<Film> findAllFilms();
    Film addFilm(Film film);
    void updateFilm(Film film);
    Film getFilmById(int id);
    boolean isContainsFilms(int id);
    void addLike(int filmId, int userId);
    void removeLike(int filmId, int userId);
    List<Film> getPopularFilms(String count);

}
