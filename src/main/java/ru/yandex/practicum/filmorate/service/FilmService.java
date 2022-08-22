package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Comparator<Film> sortedPopularFilm(){// сортированные фильмы
        return Comparator.comparing(film -> film.getAmountLikes().size(), Comparator.reverseOrder());
    }

    public Film getFilmById(int id) {// доболение по айди
        return filmStorage.getFilmById(id);
    }

    public List<Film> findAllFilms() {//поиск всех фильмов
        return filmStorage.findAllFilms();
    }

    public void addFilm(Film film) {// добовление фильма
        filmStorage.addFilm(film);
    }

    public void updateFilm(Film film) {// обновление фильма
        filmStorage.updateFilm(film);
    }

    public boolean isContainsFilms(int id) {// содержание
        return filmStorage.isContainsFilms(id);
    }

    public FilmStorage getFilmStorage() {// получние места храниея
        return filmStorage;
    }


    public void addLike(int filmId, int userId){//добовление лайка
        Film film = filmStorage.getFilmById(filmId);
        film.getAmountLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLike(int filmId, int userId){// удаление лайка
        Film film = filmStorage.getFilmById(filmId);
        film.getAmountLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(String count){// вывод 10 наиболее популярных фильмов по количеству лайков.
        return filmStorage.findAllFilms().stream()
                .filter(film -> film.getAmountLikes() != null)
                .sorted(sortedPopularFilm())
                .limit(Integer.parseInt(count))
                .collect(Collectors.toList());
    }


}
//Создайте FilmService, который будет отвечать за операции с фильмами,
// — добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
// Пусть пока каждый пользователь может поставить лайк фильму только один раз.
//Добавьте к ним аннотацию @Service — тогда к ним можно будет получить доступ из контроллера.
