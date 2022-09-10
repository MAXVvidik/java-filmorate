package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAllUsers() {// поиск юзера
        return new ArrayList<>(users.values());
    }

    @Override
    public void addUser(User user) {// добавление юзера
        users.put(user.getId(),user);

    }

    @Override
    public void updateUser(User user) {//обновление юзера
        users.put(user.getId(),user);
    }

    @Override
    public User getUserById(int id) {// получение юзера по айди
        return users.get(id);
    }

    @Override
    public boolean isContainsUser(int idUser) {// наличие пользователя
        if (users.containsKey(idUser)){
            return true;
        }else {
            return false;
        }
    }
}
//Создайте интерфейсы FilmStorage и UserStorage,
// в которых будут определены методы добавления, удаления и модификации объектов.
//Создайте классы InMemoryFilmStorage и InMemoryUserStorage,
// имплементирующие новые интерфейсы, и перенесите туда всю логику хранения, обновления и поиска объектов.
//Добавьте к InMemoryFilmStorage и InMemoryUserStorage
// аннотацию @Component, чтобы впоследствии пользоваться внедрением зависимостей и передавать хранилища сервисам.