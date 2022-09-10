package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InputDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

//добавление фильма;
//обновление фильма;
//получение всех фильмов.

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;

    }


    @GetMapping
    public List<Film> findAllFilms() {
        log.info("Получен запрос к эндпоинту: GET /films");
        return filmService.findAllFilms();
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        filmService.addFilm(film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        filmService.updateFilm(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Film getFilmByID(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту: GET /films/{id}");
        if(!filmService.isContainsFilms(id)) {
            throw new InputDataException("Фильм с таким id не найден");
        }
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) String count) {
        return filmService.getPopularFilms(count);
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        filmService.addLike(filmId, userId);

    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        filmService.removeLike(filmId, userId);

    }

}
