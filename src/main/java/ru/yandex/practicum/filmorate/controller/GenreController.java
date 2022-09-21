package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
public class GenreController {

    private final FilmService filmService;
    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/genres/{id}")
    public Genre getGenresById(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту GET /genres/{id}");
        return filmService.getGenreById(id);
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        log.info("Получен запрос к эндпоинту: GET /genres");
        return filmService.findAllGenres();
    }

}
