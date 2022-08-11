package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private Map<Long, User> users = new HashMap();
    private long counter = 0;

    @GetMapping("/users")
    public List<User> listUsers() {
        return new ArrayList<User>(users.values());
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        normalaizeUser(user);
        user.setId(++counter);
        users.put(user.getId(), user);
        log.info("Пользователь: {} успешно добавлен в каталог.", user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws UnknownObjectException {
        if(users.containsKey(user.getId())) {
            normalaizeUser(user);
            users.put(user.getId(),user);
            log.info("Данные пользователя: {} успешно обновлены в каталоге.", user);
            return user;
        } else {
            log.warn("Пользователь: {} не найден!", user);
            throw new UnknownObjectException("Пользователь не найден!");
        }
    }
    public static void normalaizeUser(User user) {
        if (user.getName().isEmpty()) {
            log.info("У переданного пользователя: {}, поле name=пусто, устанавливаем равным login.", user);
            user.setName(user.getLogin());
        }
    }
}
