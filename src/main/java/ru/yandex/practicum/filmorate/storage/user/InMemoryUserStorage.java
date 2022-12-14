package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap();
    private long counter = 0;

    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        normalizeUser(user);
        user.setId(++counter);
        user.setFriendSet(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Пользователь: {} успешно добавлен в каталог.", user);
        return user;
    }

    public User updateUser(User user) {
        if(users.containsKey(user.getId())) {
            this.normalizeUser(user);
            users.put(user.getId(),user);
            log.info("Данные пользователя: {} успешно обновлены в каталоге.", user);
            return user;
        } else {
            log.warn("Пользователь: {} не найден!", user);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такой пользователь не найден!");
        }
    }

    @Override
    public User findUserById(long userId) {
        if(users.containsKey(userId)) {
            log.info("Данные пользователя: {} успешно найдены в каталоге.", userId);
            return users.get(userId);
        } else {
            log.warn("Пользователь с userId: {} не найден!", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такой пользователь не найден!");
        }
    }

    @Override
    public void normalizeUser(User user) {
        if (user.getName().isEmpty()) {
            log.info("У переданного пользователя: {}, поле name=пусто, устанавливаем равным login.", user);
            user.setName(user.getLogin());
        }
        if (user.getFriendSet()==null) {
            user.setFriendSet(new HashSet<>());
        }
    }

}
