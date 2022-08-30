package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InputDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

//добавление фильма;
//обновление фильма;
//получение всех фильмов.

@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }


    @GetMapping("/films")
    //@ResponseBody
    public List<Film> findAllFilms() {
        log.info("Получен запрос к эндпоинту: GET /films");
        return filmService.findAllFilms();
    }

    @PostMapping("/films")
    //@ResponseBody
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        filmService.addFilm(film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping("/films")
    //@ResponseBody
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        filmService.updateFilm(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping("/films/{id}")
    //@ResponseBody
    public Film getFilmByID(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту: GET /films/{id}");
        if(!filmService.isContainsFilms(id)) {
            throw new InputDataException("Фильм с таким id не найден");
        }
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    //@ResponseBody
    public List<Film> getPopularFilms(@RequestParam(required = false) String count) {
        return filmService.getPopularFilms(count);
    }


    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getAmountLikes().add(userId);
        filmService.addLike(filmId, userId);
        filmStorage.updateFilm(film);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getAmountLikes().remove(userId);
        filmService.removeLike(filmId, userId);
        filmStorage.updateFilm(film);
    }

}
