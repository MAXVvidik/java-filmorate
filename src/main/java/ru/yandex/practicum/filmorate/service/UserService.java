package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

//Создайте UserService, который будет отвечать за такие операции с пользователями,
// как добавление в друзья, удаление из друзей, вывод списка общих друзей.
// Пока пользователям не надо одобрять заявки в друзья — добавляем сразу.
// То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public List<User> findAllUsers() {// поиск всех
        return userStorage.findAllUsers();
    }
    public void addUser(User user) {// добавление
        userStorage.addUser(user);
    }
    public void updateUser(User user) {// обновление
        userStorage.updateUser(user);
    }

    public User getUserById(int id) {//по айди
        return userStorage.getUserById(id);
    }
    public boolean isContainsUser(int idUser) {
        return userStorage.isContainsUser(idUser);
    }

    public void addFriend(int id, int friendId) {//добавление в друзья
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(friendId);
        firstUser.getFriends().add(secondUser.getId());
        secondUser.getFriends().add(firstUser.getId());
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
    }
    public void deleteFriend(int id, int friendId) {// удаление из друзей,
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(friendId);
        firstUser.getFriends().remove(secondUser.getId());
        secondUser.getFriends().remove(firstUser.getId());
        userStorage.updateUser(firstUser);
        userStorage.updateUser(secondUser);
    }
    public List<User> mutualFriends(int id, int friendId) {//То есть если Лена стала другом Саши, то это значит, что Саша теперь друг Лены.
        User firstUser = userStorage.getUserById(id);
        User secondUser = userStorage.getUserById(friendId);
        List<User> mutualFriends = new ArrayList<>();
        firstUser.getFriends().stream()
                .filter(idUser -> secondUser.getFriends().contains(idUser))
                .forEach(idUser -> mutualFriends.add(userStorage.getUserById(idUser)));
        return mutualFriends;
    }
    public List<User> getAllFriend(int id) {//  вывод списка общих друзей.
        List<User> mutualFriends = new ArrayList<>();
        User user = userStorage.getUserById(id);
        for(Integer idFriend : user.getFriends()) {
            User friend = userStorage.getUserById(idFriend);
            mutualFriends.add(friend);
        }
        return mutualFriends;
    }

}
