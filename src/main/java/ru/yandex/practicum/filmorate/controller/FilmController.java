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
import ru.yandex.practicum.filmorate.validate.FilmDataValidate;

import java.util.*;

//добавление фильма;
//обновление фильма;
//получение всех фильмов.

@RestController
@Slf4j
public class FilmController {

    private static final String POPULAR_FILMS = "10";
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    private static int id = 0;

    public int getId() {
        this.id++;
        return id;
    }

    @GetMapping("/films")
    @ResponseBody
    public List<Film> findAllFilms() {
        log.info("Получен запрос к эндпоинту: GET /films");
        return filmService.findAllFilms();
    }

    @PostMapping("/films")
    @ResponseBody
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        if(film.getAmountLikes() == null) {
            film.setAmountLikes(new HashSet<>());
        }
        if(new FilmDataValidate(film).checkAllData()) {
            log.info("Получен запрос к эндпоинту: POST /films");
            film.setId(getId());
            filmService.addFilm(film);
            return new ResponseEntity<>(film, HttpStatus.CREATED);
        } else {
            log.warn("Запрос к эндпоинту POST не обработан. Введеные данные о фильме не удовлетворяют условиям");
            throw new ValidationException("Одно или несколько из условий не выполняются.");
        }
    }

    @PutMapping("/films")
    @ResponseBody
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        if(film.getAmountLikes() == null) {
            film.setAmountLikes(new HashSet<>());
        }
        if(!filmService.isContainsFilms(film.getId())) {
            throw new InputDataException("Фильм c таким id не найден");
        }
        if(new FilmDataValidate(film).checkAllData() && film.getId() > 0) {
            log.info("Получен запрос к эндпоинту: PUT /films обновление фильма");
            filmService.updateFilm(film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            log.warn("Запрос к эндпоинту POST не обработан. Введеные данные о фильме не удовлетворяют условиям");
            throw new ValidationException("Одно или несколько из условий не выполняются.");
        }
    }

    @GetMapping("/films/{id}")
    @ResponseBody
    public Film getFilmByID(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту: GET /films/{id}");
        if(!filmService.isContainsFilms(id)) {
            throw new InputDataException("Фильм с таким id не найден");
        }
        return filmService.getFilmById(id);
    }

    @GetMapping("/films/popular")
    @ResponseBody
    public List<Film> getPopularFilms(@RequestParam(required = false) String count) {
        log.info("Получен запрос к эндпоинту: GET /films/popular");
        if(count != null) {
            return filmService.getPopularFilms(count);
        } else {
            return filmService.getPopularFilms(POPULAR_FILMS);
        }
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Получен запрос к эндпоинту: PUT /films добавление лайка к фильму " + id + ", пользователя " + userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Получен запрос к эндпоинту: DELETE /films добавление лайка к фильму " + id + ", " +
                "пользователя " + userId);
        if(!filmService.isContainsFilms(id)) {
            log.warn("Запрос к эндпоинту DELETE не обработан. Фильм с таким id не найден. id = " + id);
            throw new InputDataException("Фильм с таким id не найден");
        }
        if(userId < 0) {
            throw new InputDataException("Пользователь с таким id не найден");
        }
        filmService.removeLike(id, userId);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleIncorrectValidation(ValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        log.warn("При обработке запроса возникло исключение " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(InputDataException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
