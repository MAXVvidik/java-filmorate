package ru.yandex.practicum.filmorate.storage.film;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findAllFilms() {// поиск всех фильмов
        return new ArrayList<>(films.values());
    }

    @Override
    public void addFilm(Film film) {// добовление фильма
        films.put(film.getId(),film);
    }

    @Override
    public void updateFilm(Film film) {// обновление фильма
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilmById(int id) {//получения фильма по айди
        return films.get(id);
    }

    @Override
    public boolean isContainsFilms(int id) {// наличие фильма
        return films.containsKey(id);
    }
}

//Создайте интерфейсы FilmStorage и UserStorage,
// в которых будут определены методы добавления, удаления и модификации объектов.
//Создайте классы InMemoryFilmStorage и InMemoryUserStorage,
// имплементирующие новые интерфейсы, и перенесите туда всю логику хранения, обновления и поиска объектов.
//Добавьте к InMemoryFilmStorage и InMemoryUserStorage
// аннотацию @Component, чтобы впоследствии пользоваться внедрением зависимостей и передавать хранилища сервисам.
