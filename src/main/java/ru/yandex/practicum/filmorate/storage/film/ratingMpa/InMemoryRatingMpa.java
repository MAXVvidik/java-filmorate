package ru.yandex.practicum.filmorate.storage.film.ratingMpa;

import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRatingMpa implements RatingMpaStorage{
    @Override
    public Mpa getMpaRatingById(int id) {
        Mpa mpa = new Mpa();
        mpa.setId(id);
        mpa.setName(FilmRating.getNameString(id));
        return mpa;
    }

    @Override
    public List<Mpa> findAllMpaRatings() {
        List <Mpa> mpaAll = new ArrayList<>();
        for(String name: FilmRating.getGenresString()) {
            Mpa mpa = new Mpa();
            mpa.setName(name);
            mpaAll.add(mpa);
        }
        return mpaAll;
    }
}
