package ru.yandex.practicum.filmorate.storage.film.ratingMpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface RatingMpaStorage {
    Mpa getMpaRatingById(int id);
    List<Mpa> findAllMpaRatings();
}
