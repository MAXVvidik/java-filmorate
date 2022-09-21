package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.List;

@RestController
@Slf4j
public class RatingMpaController {
    private final FilmService filmService;
    @Autowired
    public RatingMpaController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping("/mpa/{id}")
    public Mpa getMpaRatingById(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту GET /mpa/{id}");
        return filmService.getRatingById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpaRating() {
        log.info("Получен запрос к эндпоинту: GET /mpa");
        return filmService.findAllRatings();
    }

}
