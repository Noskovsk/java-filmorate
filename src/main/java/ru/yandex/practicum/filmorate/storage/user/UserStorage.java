package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> listUsers();

    User createUser(User user);

    //TODO поработать с исключениями
    User updateUser(User user);

    User findUserById(long userId);

    void normalizeUser(User user);

}
