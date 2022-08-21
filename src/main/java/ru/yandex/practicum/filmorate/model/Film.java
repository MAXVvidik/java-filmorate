package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.time.Duration;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;//целочисленный идентификатор — id;
    @NonNull
    private String name;//название — name;
    @NonNull
    private String description;//описание — description;
    @NonNull
    private LocalDate releaseDate;//дата релиза — releaseDate;
    @NonNull
    private int duration;//продолжительность фильма — duration.

}
