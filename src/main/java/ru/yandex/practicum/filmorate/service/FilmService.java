package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InputDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.ratingMpa.RatingMpaStorage;
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
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final RatingMpaStorage ratingMpaStorage;


    private static int id = 0;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("ratingMpaDbStorage") RatingMpaStorage ratingMpaStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.ratingMpaStorage = ratingMpaStorage;
    }

    public int getId() {
        this.id++;
        return id;
    }

    public Comparator<Film> sortPopularFilm() {
        return Comparator.comparing(film -> film.getAmountLikes().size(), Comparator.reverseOrder());
    }
    public Film getFilmById(int id) {
        log.info("Получен запрос к эндпоинту: GET /films/{id}");
        if(!this.isContainsFilms(id)) {
            throw new InputDataException("Фильм с таким id не найден");
        }
        return filmStorage.getFilmById(id);
    }
    public List<Film> findAllFilms() {// поиск всех фильмов
        log.info("Получен запрос к эндпоинту: GET /films");
        return filmStorage.findAllFilms();
    }

    public void addFilm(Film film) {// добавление фильма
        if(film.getAmountLikes() == null) {
            film.setAmountLikes(new HashSet<>());
        }
        if(new FilmDataValidate(film).checkAllData()) {
            log.info("Получен запрос к эндпоинту: POST /films");
            film.setId(film.getId());
            filmStorage.addFilm(film);
        } else {
            log.warn("Запрос к эндпоинту POST не обработан. Введенные данные о фильме не удовлетворяют условиям");
            throw new ValidationException("Одно или несколько из условий не выполняются.");
        }
    }
    public void updateFilm(Film film) {// обновления
        if(film.getAmountLikes() == null) {
            film.setAmountLikes(new HashSet<>());
        }
        if(!this.isContainsFilms(film.getId())) {
            throw new InputDataException("Фильм c таким id не найден");
        }
        if(new FilmDataValidate(film).checkAllData() && film.getId() > 0) {
            log.info("Получен запрос к эндпоинту: PUT /films обновление фильма");
            filmStorage.updateFilm(film);
        } else {
            log.warn("Запрос к эндпоинту POST не обработан. Введенные данные о фильме не удовлетворяют условиям");
            throw new ValidationException("Одно или несколько из условий не выполняются.");
        }
    }
    public boolean isContainsFilms(int id) {
        return filmStorage.isContainsFilms(id);
    }

    public void addLike(int filmId, int userId) {// добавление лайка,
        Film film = filmStorage.getFilmById(filmId);
        film.getAmountLikes().add(userId);
        filmStorage.updateFilm(film);//  в сервисе
        log.info("Получен запрос к эндпоинту: PUT /films добавление лайка к фильму " + filmId + ", пользователя " + userId);
    }

    public void removeLike(int filmId, int userId) {// удаление лайка,
        Film film = filmStorage.getFilmById(filmId);
        film.getAmountLikes().remove(userId);
        log.info("Получен запрос к эндпоинту: DELETE /films добавление лайка к фильму " + filmId + ", " +
                "пользователя " + userId);
        if(!this.isContainsFilms(filmId) && userId < 0) {
            log.warn("Запрос к эндпоинту DELETE не обработан. Фильм с таким id не найден. id = " + filmId);
            throw new InputDataException("Фильм с таким id не найден или Пользователь с таким id не найден" );
        } else {
            filmStorage.updateFilm(film);// в сервисе
        }
    }

    public List<Film> getPopularFilms(String count) {//  вывод 10 наиболее популярных фильмов
        log.info("Получен запрос к эндпоинту: GET /films/popular");
        if(count != null) {
            return filmStorage.findAllFilms().stream()
                    .filter(film -> film.getAmountLikes() != null)
                    .sorted(sortPopularFilm())
                    .limit(Integer.parseInt(count))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public Genre getGenreById(int id) {// поиск жанров
        if(id < 1) {
            log.warn("Запрос к эндпоинту GET не обработан. Жанр с таким id не найден. id = " + id);
            throw new InputDataException("Некорректный id жанра");
        }
        return genreStorage.getGenreById(id);
    }


    public Mpa getRatingById(int id) {// поиск рейтинга
        if(id < 1) {
            log.warn("Запрос к эндпоинту GET не обработан. Рейтинг с таким id не найден. id = " + id);
            throw new InputDataException("Некорректный id рейтинга");
        }
        return ratingMpaStorage.getMpaRatingById(id);
    }

    public List<Genre> findAllGenres() {// лист всех жанров
        return genreStorage.findAllGenres();
    }

    public List<Mpa> findAllRatings() {// лист рейтингов
        return ratingMpaStorage.findAllMpaRatings();
    }
}
