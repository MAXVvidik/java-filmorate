package ru.yandex.practicum.filmorate.storage.film;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InputDataException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findAllFilms() {// поиск всех фильмов
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {// добовление фильма
        films.put(film.getId(),film);
        return film;
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

    @Override
    public void addLike(int filmId, int userId) {
        Film film = films.get(filmId);
        film.getAmountLikes().add(userId);
        updateFilm(film);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if(!isContainsFilms(filmId)) {
            log.warn("Запрос к эндпоинту DELETE не обработан. Фильм с таким id не найден. id = " + filmId);
            throw new InputDataException("Фильм с таким id не найден");
        }
        if(userId < 0) {
            throw new InputDataException("Пользователь с таким id не найден");
        }
        Film film = films.get(filmId);
        film.getAmountLikes().remove(userId);
        updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(String count) {
        return findAllFilms().stream()
                .filter(film -> film.getAmountLikes() != null)
                .sorted(sortPopularFilm())
                .limit(Integer.parseInt(count))
                .collect(Collectors.toList());
    }

    public Comparator<Film> sortPopularFilm() {
        return Comparator.comparing(film -> film.getAmountLikes().size(), Comparator.reverseOrder());
    }

}

//Создайте интерфейсы FilmStorage и UserStorage,
// в которых будут определены методы добавления, удаления и модификации объектов.
//Создайте классы InMemoryFilmStorage и InMemoryUserStorage,
// имплементирующие новые интерфейсы, и перенесите туда всю логику хранения, обновления и поиска объектов.
//Добавьте к InMemoryFilmStorage и InMemoryUserStorage
// аннотацию @Component, чтобы впоследствии пользоваться внедрением зависимостей и передавать хранилища сервисам.
