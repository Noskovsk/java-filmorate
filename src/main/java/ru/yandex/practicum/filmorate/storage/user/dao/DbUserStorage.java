package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("DbUserStorage")
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> listUsers() {
        String userRows = "SELECT * FROM \"User\"";
        return jdbcTemplate.query(userRows, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User createUser(User user) {
        normalizeUser(user);
        user.setFriendSet(new HashSet<>());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertSql = "INSERT INTO \"User\" (\"name\", \"email\", \"login\", \"birthdate\") VALUES (?, ?, ?, ?)";
        Integer rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKeyAs(Long.class));
        log.info("Пользователь: {} успешно добавлен в каталог. Всего добавлено строк: {}", user, rows);
        return user;
    }

    @Override
    public User updateUser(User user) {
        findUserById(user.getId());
        this.normalizeUser(user);
        String updateSql = "UPDATE \"User\" SET \"name\"=?, \"email\"=?, \"login\"=?, \"birthdate\"=? WHERE \"user_id\"=?";
        Integer rows = jdbcTemplate.update(updateSql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        if (rows == 1) {
            log.info("Данные пользователя: {} успешно обновлены в каталоге. Обновлено строк: {}", user.getId(), rows);
            return user;
        } else {
            log.warn("Что-то пошло не так! Обновлено строк: {}", rows);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Что-то пошло не так!");
        }
    }

    @Override
    public User findUserById(long userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"User\" WHERE \"user_id\" = ?", userId);
        if (userRows.next()) {
            User user = makeUser(userRows);
            log.info("Найден пользователь c id: {}, логин: {}", user.getId(), user.getLogin());
            return user;
        } else {
            log.warn("Пользователь с userId: {} не найден!", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такой пользователь не найден!");
        }
    }

    private User makeUser(SqlRowSet sqlUserRow) {
        return new User(sqlUserRow.getLong("user_id"),
                sqlUserRow.getString("email"),
                sqlUserRow.getString("login"),
                sqlUserRow.getString("name"),
                sqlUserRow.getDate("birthdate").toLocalDate(), new HashSet<>());
    }

    private User makeUser(ResultSet sqlUserResultSet) throws SQLException {
        return new User(sqlUserResultSet.getLong("user_id"),
                sqlUserResultSet.getString("email"),
                sqlUserResultSet.getString("login"),
                sqlUserResultSet.getString("name"),
                sqlUserResultSet.getDate("birthdate").toLocalDate(), new HashSet<>());
    }

    @Override
    public void normalizeUser(User user) {
        if (user.getName().isEmpty()) {
            log.info("У переданного пользователя: {}, поле name=пусто, устанавливаем равным login.", user);
            user.setName(user.getLogin());
        }
    }

    @Override
    public void addFriend(long userId, long friendId) {
        findUserById(userId);
        findUserById(friendId);
        String insertSql = "INSERT INTO \"Friendship\" (\"user_id\", \"friend_id\") VALUES (?, ?)";
        Integer rows = jdbcTemplate.update(insertSql, userId, friendId);
        log.info("Пользователи с id: {} добавил в друзья пользователя с id: {}. Всего добавлено строк: {}", userId, friendId, rows);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        findUserById(userId);
        findUserById(friendId);
        String deleteSql = "DELETE FROM \"Friendship\" WHERE (\"user_id\"= ? AND \"friend_id\"=?)";
        Integer rows = jdbcTemplate.update(deleteSql, userId, friendId);
        if (rows == 1) {
            log.info("Пользователи с id: {} удалил из друзей пользователя с id: {}. Удалено строк {}:", userId, friendId, rows);
        } else {
            log.warn("Что-то пошло не так! Обновлено строк: {}", rows);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Что-то пошло не так!");
        }
    }

    @Override
    public Set<Long> getFriendsId(User user) {
        String selectSql = "SELECT * FROM \"Friendship\" WHERE \"user_id\" = ?";
        return jdbcTemplate.query(selectSql,
                (rs, rowNum) -> rs.getLong("friend_id"),
                user.getId()).stream().collect(Collectors.toSet());
    }

}
