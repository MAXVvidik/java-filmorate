package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InputDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validate.FilmDataValidate;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

//Создайте FilmService, который будет отвечать за операции с фильмами,
// — добавление и удаление лайка, вывод 10 наиболее популярных фильмов по количеству лайков.
// Пусть пока каждый пользователь может поставить лайк фильму только один раз.

@Service
@Slf4j
public class FilmService {
    private static final String POPULAR_FILMS = "10" ;
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmService(InMemoryFilmStorage fileStorage, FilmService filmService) {
        this.filmStorage = fileStorage;
        this.filmService = filmService;
    }

    public Comparator<Film> sortPopularFilm() {
        return Comparator.comparing(film -> film.getAmountLikes().size(), Comparator.reverseOrder());
    }
    public Film getFilmById(int id) {
        log.info("Получен запрос к эндпоинту: GET /films/{id}");
        if(!filmService.isContainsFilms(id)) {
            throw new InputDataException("Фильм с таким id не найден");
        }
        return filmService.getFilmById(id);
    }
    public List<Film> findAllFilms() {// поиск всех фильмов
        log.info("Получен запрос к эндпоинту: GET /films");
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {// добавление фильма
        if (film.getAmountLikes() == null) {
            film.setAmountLikes(new HashSet<>());
        }
        if (new FilmDataValidate(film).checkAllData()) {
            log.info("Получен запрос к эндпоинту: POST /films");
            film.setId(film.getId());
        } else {
            log.warn("Запрос к эндпоинту POST не обработан. Введеные данные о фильме не удовлетворяют условиям");
            throw new ValidationException("Одно или несколько из условий не выполняются.");
        }
        return film;
    }
    public void updateFilm(Film film) {// обновления
        if(film.getAmountLikes() == null) {
            film.setAmountLikes(new HashSet<>());
        }
        if(!filmService.isContainsFilms(film.getId())) {
            throw new InputDataException("Фильм c таким id не найден");
        }
        if(new FilmDataValidate(film).checkAllData() && film.getId() > 0) {
            log.info("Получен запрос к эндпоинту: PUT /films обновление фильма");
            filmService.updateFilm(film);
        } else {
            log.warn("Запрос к эндпоинту POST не обработан. Введеные данные о фильме не удовлетворяют условиям");
            throw new ValidationException("Одно или несколько из условий не выполняются.");
        }
    }
    public boolean isContainsFilms(int id) {
        return filmStorage.isContainsFilms(id);
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public void addLike(int filmId, int userId) {// добавление лайка,
        log.info("Получен запрос к эндпоинту: PUT /films добавление лайка к фильму " + filmId + ", пользователя " + userId);
        filmService.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {// удаление лайка,
        log.info("Получен запрос к эндпоинту: DELETE /films добавление лайка к фильму " + filmId + ", " +
                "пользователя " + userId);
        if(!filmService.isContainsFilms(filmId)) {
            log.warn("Запрос к эндпоинту DELETE не обработан. Фильм с таким id не найден. id = " + filmId);
            throw new InputDataException("Фильм с таким id не найден");
        }
        if(userId < 0) {
            throw new InputDataException("Пользователь с таким id не найден");
        }
        filmService.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(String count) {//  вывод 10 наиболее популярных фильмов
        log.info("Получен запрос к эндпоинту: GET /films/popular");
        if (count != null) {
            return filmService.getPopularFilms(count);
        } else {
            return filmService.getPopularFilms(POPULAR_FILMS);
        }
    }

}