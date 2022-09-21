package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();
    User addUser(User user);
    void updateUser(User user);
    User getUserById(int id);
    boolean isContainsUser(int id);
    List<User> mutualFriends(int id, int friendId);
    List<User> getAllFriend(int id);
    void addFriend(int id, int friendId);
    void deleteFriend(int id, int friendId);
}