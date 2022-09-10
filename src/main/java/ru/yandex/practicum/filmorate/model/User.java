package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
    @Builder.Default
    private Set<Integer> friends = new HashSet<>();
}