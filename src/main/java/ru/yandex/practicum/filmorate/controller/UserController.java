package ru.yandex.practicum.filmorate.controller;

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
public class UserController {
    private Map<Long, User> users = new HashMap();
    private long counter = 0;

    @GetMapping("/users")
    public List<User> listUsers() {
        return new ArrayList<User>(users.values());
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        user.normalaize();
        user.setId(++counter);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws UnknownObjectException {
        if(users.containsKey(user.getId())) {
            user.normalaize();
            users.put(user.getId(),user);
            return user;
        } else {
            throw new UnknownObjectException("Такой пользователь не найден!");
        }
    }
}
