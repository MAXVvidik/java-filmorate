package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
public interface FilmStorage {
    List<Film> findAllFilms();
    void addFilm(Film film);
    void updateFilm(Film film);
    Film getFilmById(int id);
    boolean isContainsFilms(int id);

}
