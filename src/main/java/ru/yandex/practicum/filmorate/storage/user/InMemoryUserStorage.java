package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        users.put(user.getId(), user);
        log.info("Пользователь: {} успешно добавлен в каталог.", user);
        return user;
    }

    public User updateUser(User user) throws UnknownObjectException {
        if(users.containsKey(user.getId())) {
            this.normalizeUser(user);
            users.put(user.getId(),user);
            log.info("Данные пользователя: {} успешно обновлены в каталоге.", user);
            return user;
        } else {
            log.warn("Пользователь: {} не найден!", user);
            throw new UnknownObjectException("Пользователь не найден!");
        }
    }

    @Override
    public void normalizeUser(User user) {
        if (user.getName().isEmpty()) {
            log.info("У переданного пользователя: {}, поле name=пусто, устанавливаем равным login.", user);
            user.setName(user.getLogin());
        }
    }

}
