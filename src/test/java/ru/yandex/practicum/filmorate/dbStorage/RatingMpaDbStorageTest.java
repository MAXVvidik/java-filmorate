package ru.yandex.practicum.filmorate.dbStorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.ratingMpa.RatingMpaDbStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingMpaDbStorageTest {

    private final RatingMpaDbStorage ratingMpaDbStorage;

    @Test
    public void getGenreById() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        assertEquals(mpa, ratingMpaDbStorage.getMpaRatingById(1));
    }
    @Test
    public void getAllGenres() {
        List<Mpa> ratings = new ArrayList<>();
        int id = 1;
        Mpa mpa1 = new Mpa();
        mpa1.setId(id++);
        mpa1.setName("G");
        ratings.add(mpa1);
        Mpa mpa2 = new Mpa();
        mpa2.setId(id++);
        mpa2.setName("PG");
        ratings.add(mpa2);
        Mpa mpa3 = new Mpa();
        mpa3.setId(id++);
        mpa3.setName("PG-13");
        ratings.add(mpa3);
        Mpa mpa4 = new Mpa();
        mpa4.setId(id++);
        mpa4.setName("R");
        ratings.add(mpa4);
        Mpa mpa5 = new Mpa();
        mpa5.setId(id++);
        mpa5.setName("NC-17");
        ratings.add(mpa5);

        assertEquals(ratings, ratingMpaDbStorage.findAllMpaRatings());
    }
}
