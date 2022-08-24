package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InputDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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

    private static int id = 0;

    public int getId() {
        this.id++;
        return id;
    }

    @GetMapping("/films")
    @ResponseBody
    public List<Film> findAllFilms() {//исправил
        return filmService.findAllFilms();
    }

    @PostMapping("/films")
    @ResponseBody
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {// исправил
        return new ResponseEntity<>(filmService.addFilm(film), HttpStatus.CREATED);
    }

    @PutMapping("/films")
    @ResponseBody
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {//исправил
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping("/films/{id}")
    @ResponseBody
    public Film getFilmByID(@PathVariable("id") int id) {//исправил
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    @ResponseBody
    public List<Film> getPopularFilms(@RequestParam(required = false) String count) {//исправил
        return filmStorage.findAllFilms().stream()
                .filter(film -> film.getAmountLikes() != null)
                .sorted(filmService.sortPopularFilm())
                .limit(Integer.parseInt(count))
                .collect(Collectors.toList());

    }

    @PutMapping("/films/{id}/like/{userId}")//исправил
    public void addLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getAmountLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getAmountLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

}
