package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InputDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validate.UserDataValidate;

import java.util.*;

//создание пользователя;
//обновление пользователя;
//получение списка всех пользователей.

@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    private static int id = 0;

    public int getId() {
        this.id++;
        return id;
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        log.info("Получен запрос к эндпоинту: GET /users");
        return userService.findAllUsers();
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Получен запрос к эндпоинту: POST /users");
        if(user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if(user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if(new UserDataValidate(user).checkAllData()) {
            user.setId(getId());
            userService.addUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } else {
            log.warn("Запрос к эндпоинту POST /users не обработан.");
            throw new ValidationException("Одно или несколько условий не выполняются");
        }
    }
    @PutMapping("/users")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        log.info("Получен запрос к эндпоинту: PUT /users");
        if(!userService.isContainsUser(user.getId())) {
            throw new InputDataException("Пользователь с таким id не найден");
        }
        if(user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if(user.getName().isEmpty()) {
            user.setName(user.getEmail());
        }
        if(new UserDataValidate(user).checkAllData() && user.getId() > 0) {
            userService.updateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            log.warn("Запрос к эндпоинту PUT /users не обработан.");
            throw new ValidationException("Одно или несколько условий не выполняются");
        }
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту: GET /users/{id}");
        if(!userService.isContainsUser(id)) {
            log.warn("Пользователь с таким id не найден, id=" + id);
            throw new InputDataException("Пользователь с таким id не найден");
        }
        return userService.getUserById(id);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable("id") int id) {
        log.info("Получен запрос к эндпоинту: GET /users/{id}/friends");
        return userService.getAllFriend(id);
    }
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        log.info("Получен запрос к эндпоинту: GET /users/{id}/friends/common/{otherId}");
        if (!userService.isContainsUser(id) || !userService.isContainsUser(otherId)) {
            log.warn("Пользователь с таким id не найден, id1=" + id + ", id2=" + otherId);
            throw new InputDataException("Один из двух друзей не найден по своему id");
        }
        return userService.mutualFriends(id, otherId);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        log.info("Получен запрос к эндпоинту: PUT /users/{id}/friends/{friendId}");
        if(!userService.isContainsUser(id) || !userService.isContainsUser(friendId)) {
            log.warn("Один или оба пользователя не найдены в базе данных по id; id1=" + id + ", id2=" +friendId);
            throw new InputDataException("Один или оба пользователя не найдены");
        }
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        log.info("Получен запрос к эндпоинту: DELETE /users/{id}/friends/{friendId}");
        userService.deleteFriend(id, friendId);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleIncorrectValidation(ValidationException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    @ResponseBody
    public ResponseEntity<String> handleException(Exception e) {
        log.warn("При обработке запроса возникло исключение " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(InputDataException e) {
        log.warn("При обработке запроса возникло исключение: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
