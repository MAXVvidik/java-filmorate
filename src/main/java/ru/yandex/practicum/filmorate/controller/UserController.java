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
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validate.UserDataValidate;

import java.util.*;

//создание пользователя;
//обновление пользователя;
//получение списка всех пользователей.

@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserStorage userStorage;

    private static int id = 0;

    public int getId() {
        this.id++;
        return id;
    }

    @Autowired
    public UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @PostMapping("/users")
    @ResponseBody
    public ResponseEntity<User> createUser(@RequestBody User user) {
        userService.addUser(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        userStorage.updateUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") int id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable("id") int id) {
        List<User> mutualFriends = new ArrayList<>();
        User user = userStorage.getUserById(id);
        for(Integer idFriend : user.getFriends()) {
            User friend = userStorage.getUserById(idFriend);
            mutualFriends.add(friend);
        }
        return mutualFriends;
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(otherId);
        List<User> mutualFriends = new ArrayList<>();
        firstUser.getFriends().stream()
                .filter(idUser -> secondUser.getFriends().contains(idUser))
                .forEach(idUser -> mutualFriends.add(userStorage.getUserById(idUser)));
        return mutualFriends;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(friendId);
        firstUser.getFriends().add(secondUser.getId());
        secondUser.getFriends().add(firstUser.getId());
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(friendId);
        firstUser.getFriends().remove(secondUser.getId());
        secondUser.getFriends().remove(firstUser.getId());
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);

    }

}
