package ru.yandex.practicum.filmorate.dbStorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreDbStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    public void getGenreById() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        assertEquals(genre, genreDbStorage.getGenreById(1));
    }

    @Test
    public void getAllGenres() {
        int id = 1;
        List<Genre> genres = new ArrayList<>();
        Genre genre1 = new Genre();
        genre1.setId(id++);
        genre1.setName("Комедия");
        genres.add(genre1);
        Genre genre2 = new Genre();
        genre2.setId(id++);
        genre2.setName("Драма");
        genres.add(genre2);
        Genre genre3 = new Genre();
        genre3.setId(id++);
        genre3.setName("Мультфильм");
        genres.add(genre3);
        Genre genre4 = new Genre();
        genre4.setId(id++);
        genre4.setName("Триллер");
        genres.add(genre4);
        Genre genre5 = new Genre();
        genre5.setId(id++);
        genre5.setName("Документальный");
        genres.add(genre5);
        Genre genre6 = new Genre();
        genre6.setId(id++);
        genre6.setName("Боевик");
        genres.add(genre6);

        assertEquals(genres, genreDbStorage.findAllGenres());
    }
}
