package ru.yandex.practicum.filmorate.dbStorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    static User user1 = User.builder()
            .name("name")
            .email("email@yandex.ru")
            .login("login")
            .birthday(LocalDate.of(1994, 10, 01))
            .build();
    static User user2 = User.builder()
            .name("name2")
            .email("email@yandex222.ru")
            .login("login222")
            .birthday(LocalDate.of(1994, 10, 01))
            .build();
    static User user3 = User.builder()
            .name("name3")
            .email("email@yandex333.ru")
            .login("login333")
            .birthday(LocalDate.of(1989, 10, 01))
            .build();

    @Test
    public void getUserById() {
        assertEquals(user1, userDbStorage.getUserById(1));
    }
    @Test
    public void getAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        assertEquals(users, userDbStorage.findAllUsers());
    }

    @Test
    public void updateUser() {
        User updateUser1 = User.builder()
                .name("Update name")
                .email("email@yandex.ru")
                .login("loginUpdate")
                .birthday(LocalDate.of(1994, 10, 01))
                .build();
        updateUser1.setId(1);
        userDbStorage.updateUser(updateUser1);
        assertEquals(updateUser1, userDbStorage.getUserById(1));
        User returnUser1 = User.builder()
                .id(1)
                .name("name")
                .email("email@yandex.ru")
                .login("login")
                .birthday(LocalDate.of(1994, 10, 01))
                .build();
        userDbStorage.updateUser(returnUser1);
    }

    @Test
    public void isContainsUser() {
        assertTrue(userDbStorage.isContainsUser(1));
    }

    @Test
    public void allAboutFriends() {
        userDbStorage.addFriend(1, 2);//TODO Добавление в друзья и показ друзей
        userDbStorage.addFriend(2, 1);
        userDbStorage.addFriend(1, 3);
        userDbStorage.addFriend(3, 1);
        List<User> userFriends = new ArrayList<>();
        userFriends.add(user2);
        userFriends.add(user3);
        assertEquals(userFriends, userDbStorage.getAllFriend(1));


        List<User> mutFriends= new ArrayList<>();//TODO Общие друзья
        mutFriends.add(user1);
        assertEquals(mutFriends, userDbStorage.mutualFriends(2,3));


        userDbStorage.deleteFriend(2,1);//TODO Удаление друга
        assertEquals(new ArrayList<User>(), userDbStorage.getAllFriend(2));
    }

    @BeforeEach
    private void addUsers() {
        userDbStorage.addUser(user1);
        userDbStorage.addUser(user2);
        userDbStorage.addUser(user3);
        user1.setId(1);
        user2.setId(2);
        user3.setId(3);
    }
}
