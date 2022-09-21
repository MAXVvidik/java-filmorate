package ru.yandex.practicum.filmorate.dbStorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;

    private final UserDbStorage userDbStorage;

    static final LinkedHashSet<Genre> genres1 = new LinkedHashSet<>();
    static final LinkedHashSet<Genre> genres2 = new LinkedHashSet<>();

    static final Mpa mpa1 = new Mpa();
    static final Mpa mpa2 = new Mpa();


    static User user1 = User.builder()
            .name("name")
            .email("email@yandex.ru")
            .login("login")
            .birthday(LocalDate.of(1994, 10, 01))
            .build();
    static User user2 = User.builder()
            .name("name2")
            .email("email@yandex222.ru")
            .login("login222")
            .birthday(LocalDate.of(1994, 10, 01))
            .build();
    static User user3 = User.builder()
            .name("name3")
            .email("email@yandex333.ru")
            .login("login333")
            .birthday(LocalDate.of(1989, 10, 01))
            .build();

    static Film film1 = Film.builder()
            .name("Film1")
            .description("description1")
            .duration(120)
            .releaseDate(LocalDate.of(2021, 5, 21))
            .rate(10)
            .mpa(mpa1)
            .genres(genres1)
            .build();

    static Film film2 = Film.builder()
            .name("Film2")
            .description("description2")
            .duration(120)
            .releaseDate(LocalDate.of(2015, 5, 21))
            .rate(10)
            .mpa(mpa2)
            .genres(genres2)
            .build();


    @Test
    public void getUserById() {
        filmDbStorage.getFilmById(1);
        assertEquals(film1, filmDbStorage.getFilmById(1));
    }

    @Test
    public void getAllFilms() {
        List<Film> films = new ArrayList<>();
        films.add(film1);
        films.add(film2);
        assertEquals(films, filmDbStorage.findAllFilms());
    }

    @Test
    public void addDeleteLikeAndGetPopularFilms() {
        if(!userDbStorage.isContainsUser(1)) {
            addUsers();
        }
        filmDbStorage.addLike(2,1);
        filmDbStorage.addLike(2,2);
        filmDbStorage.addLike(1,2);
        List<Film> popFilm1 = new ArrayList<>();
        popFilm1.add(film2);
        popFilm1.add(film1);
        assertEquals(popFilm1, filmDbStorage.getPopularFilms("2"));

        //удаляем лайки со второго фильма и добавлем к первомую.Ожидаем что на первом месте будет фильм1
        filmDbStorage.removeLike(2,1);
        filmDbStorage.addLike(1,1);
        List<Film> popFilm2 = new ArrayList<>();
        popFilm2.add(film1);
        popFilm2.add(film2);
        assertEquals(popFilm2, filmDbStorage.getPopularFilms("2"));
    }

    @Test
    public void updateFilm() {
        film1 = Film.builder()
                .id(1)
                .name("Film1Update")
                .description("description1Update")
                .duration(120)
                .releaseDate(LocalDate.of(2021, 5, 21))
                .rate(10)
                .mpa(mpa1)
                .genres(genres1)
                .build();
        filmDbStorage.updateFilm(film1);
        assertEquals(film1, filmDbStorage.getFilmById(1));

        //Возвращаем значение film1 для других тестов
        film1 = film1 = Film.builder()
                .id(1)
                .name("Film1")
                .description("description1")
                .duration(120)
                .releaseDate(LocalDate.of(2021, 5, 21))
                .rate(10)
                .mpa(mpa1)
                .genres(genres1)
                .build();
        filmDbStorage.updateFilm(film1);
    }

    @BeforeEach
    private void addFilms() {
        //Заполняем жанры и рейтинг для фильма 1
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");

        genres1.add(genre1);
        genres1.add(genre2);

        mpa1.setId(1);
        mpa1.setName("G");

        //Заполняем жанры и рейтинг для фильма 2
        Genre genre3 = new Genre();
        genre3.setId(3);
        genre3.setName("Мультфильм");

        Genre genre4 = new Genre();
        genre4.setId(4);
        genre4.setName("Триллер");

        genres2.add(genre3);
        genres2.add(genre4);

        mpa2.setId(2);
        mpa2.setName("PG");


        filmDbStorage.addFilm(film1);
        filmDbStorage.addFilm(film2);

        film1.setId(1);
        film1.setGenres(genres1);
        film1.setMpa(mpa1);
        film2.setId(2);
        film2.setGenres(genres2);
        film2.setMpa(mpa2);
    }

    private void addUsers() {
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);
        user1.setId(1);
        user2.setId(2);
        user3.setId(3);
    }
}
