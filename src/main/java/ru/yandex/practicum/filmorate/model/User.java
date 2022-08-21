package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;//целочисленный идентификатор — id;
    @NonNull
    private String email; //электронная почта — email;
    @NonNull
    private String login;//логин пользователя — login;
    @NonNull
    private String name;//имя для отображения — name;
    @NonNull
    private LocalDate birthday;//дата рождения — birthday.
}