package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

public enum FilmRating {

    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private String name;
    FilmRating(String name) {
        this.name = name;
    }
    public String getNameRating() {
        return name;
    }

    public static FilmRating checkRating(String str) {// поиск жанров
        for (FilmRating rating : FilmRating.values()) {
            if(str.equals(rating.getNameRating())) {
                return rating;
            }
        }
        throw new ValidationException("Имя рейтинга нет в базе данных");
    }

    public static String getNameString(int id) {
        List<FilmRating>genresName = List.of(FilmRating.values());
        return genresName.get(id -1).getNameRating();
    }

    public static List<String> getGenresString() {
        List<String> str = new ArrayList<>();
        for(FilmRating rating : FilmRating.values()) {
            str.add(rating.getNameRating());
        }
        return str;
    }
}
