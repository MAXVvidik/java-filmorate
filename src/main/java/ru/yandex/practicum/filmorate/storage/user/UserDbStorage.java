package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users SET name = ?, birthday = ?, email = ?, login = ?";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setDate(2, java.sql.Date.valueOf(user.getBirthday()));
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getLogin());
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;

    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE users SET name = ?, birthday = ?, email = ?, login = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getName(), java.sql.Date.valueOf(user.getBirthday()), user.getEmail(),
                user.getLogin(), user.getId());

    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
    }

    @Override
    public boolean isContainsUser(int id) {
        String sql = "SELECT user_id FROM users";
        List<Integer> queryForColumn = jdbcTemplate.queryForList(sql, Integer.class);
        return queryForColumn.contains(id);
    }

    @Override
    public List<User> mutualFriends(int id, int friendId) {
        String sqlFriends1 = "SELECT user2_id FROM friends WHERE user1_id = ?";
        List<Integer> friends_userId1 = jdbcTemplate.queryForList(sqlFriends1, Integer.class, id);
        String sqlFriends2 = "SELECT user2_id FROM friends WHERE user1_id = ?";
        List<Integer> friends_userId2 = jdbcTemplate.queryForList(sqlFriends2, Integer.class, friendId);
        List<Integer> mutualFriendsId = new ArrayList<>();
        for(Integer idUser : friends_userId1) {
            if(friends_userId2.contains(idUser)) {
                mutualFriendsId.add(idUser);
            }
        }
        List<User> mutualFriends = new ArrayList<>();
        for(Integer mutualId : mutualFriendsId) {
            mutualFriends.add(getUserById(mutualId));
        }
        return mutualFriends;
    }

    @Override
    public List<User> getAllFriend(int id) {
        String sql = "SELECT users.user_id, users.name, users.birthday, users.email, users.login " +
                "FROM users LEFT JOIN friends f ON users.user_id = f.user2_id WHERE user1_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, id);
    }

    @Override
    public void addFriend(int id, int friendId) {
        String sql = "INSERT INTO friends(user1_id, user2_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, friendId);
        String sqlFriends2 = "SELECT user2_id FROM friends WHERE USER1_ID = ?";
        List<Integer> friends_userId2 = jdbcTemplate.queryForList(sqlFriends2, Integer.class, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        String sql = "DELETE FROM friends WHERE user1_id = ? AND USER2_ID = ?";
        jdbcTemplate.update(sql, id, friendId);

    }
    public User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(Objects.requireNonNull(resultSet.getDate("birthday")).toLocalDate())
                .build();
    }
}
