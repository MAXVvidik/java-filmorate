package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.InputDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validate.UserDataValidate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//Создайте UserService, который будет отвечать за такие операции с пользователями,
// как добавление в друзья, удаление из друзей, вывод списка общих друзей.
// Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
// То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    //private final UserController userController;
    private final UserService userService;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage, UserController userController, UserService userService) {
        this.userStorage = inMemoryUserStorage;
        //this.userController = userController;
        this.userService = userService;
    }

    public List<User> findAllUsers() {// поиск всех
        return userStorage.findAllUsers();
    }
    public void addUser(User user) {// добавление
        log.info("Получен запрос к эндпоинту: POST /users");
        if(user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if(user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if(new UserDataValidate(user).checkAllData()) {
            user.setId(user.getId());
            userService.addUser(user);
        } else {
            log.warn("Запрос к эндпоинту POST /users не обработан.");
            throw new ValidationException("Одно или несколько условий не выполняются");
        }
    }
    public void updateUser(User user) {// обновление
        log.info("Получен запрос к эндпоинту: PUT /users");
        if (!userService.isContainsUser(user.getId())) {
            throw new InputDataException("Пользователь с таким id не найден");
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getEmail());
        }
        if (new UserDataValidate(user).checkAllData() && user.getId() > 0) {
            userService.updateUser(user);
        } else {
            log.warn("Запрос к эндпоинту PUT /users не обработан.");
            throw new ValidationException("Одно или несколько условий не выполняются");

        }
    }

    public User getUserById(int id) {//по айди
        log.info("Получен запрос к эндпоинту: GET /users/{id}");
        if(!userService.isContainsUser(id)) {
            log.warn("Пользователь с таким id не найден, id=" + id);
            throw new InputDataException("Пользователь с таким id не найден");
        }
        return userService.getUserById(id);
    }
    private boolean isContainsUser(int idUser) {
        return userStorage.isContainsUser(idUser);
    }

    public void addFriend(int id, int friendId) {//добавление в друзья
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(friendId);
        firstUser.getFriends().add(secondUser.getId());
        secondUser.getFriends().add(firstUser.getId());
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
        log.info("Получен запрос к эндпоинту: PUT /users/{id}/friends/{friendId}");
        if(!userService.isContainsUser(id) || !userService.isContainsUser(friendId)) {
            log.warn("Один или оба пользователя не найдены в базе данных по id; id1=" + id + ", id2=" +friendId);
            throw new InputDataException("Один или оба пользователя не найдены");
        }
    }
    public void deleteFriend(int id, int friendId) {//
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(friendId);
        firstUser.getFriends().remove(secondUser.getId());
        secondUser.getFriends().remove(firstUser.getId());
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
        log.info("Получен запрос к эндпоинту: DELETE /users/{id}/friends/{friendId}");
    }

    public List<User> mutualFriends(int id,  int otherId) {//То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(id);
        List<User> mutualFriends = new ArrayList<>();
        firstUser.getFriends().stream()
                .filter(idUser -> secondUser.getFriends().contains(idUser))
                .forEach(idUser -> mutualFriends.add(userStorage.getUserById(idUser)));
        log.info("Получен запрос к эндпоинту: GET /users/{id}/friends/common/{otherId}");
        if (!userService.isContainsUser(id) || !userService.isContainsUser(otherId)) {
            log.warn("Пользователь с таким id не найден, id1=" + id + ", id2=" + otherId);
            throw new InputDataException("Один из двух друзей не найден по своему id");
        }
        return mutualFriends;

    }
    public List<User> getAllFriend(int id) {//  вывод списка общих друзей.
        log.info("Получен запрос к эндпоинту: GET /users/{id}/friends");
        List<User> mutualFriends = new ArrayList<>();
        User user = userStorage.getUserById(id);
        for(Integer idFriend : user.getFriends()) {
            User friend = userStorage.getUserById(idFriend);
            mutualFriends.add(friend);
        }
        return mutualFriends;
    }
}

