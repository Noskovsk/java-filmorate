package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    List<User> listUsers();

    User createUser(User user);

    User updateUser(User user);

    User findUserById(long userId);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    Set<Long> getFriendsId(User user);

    void normalizeUser(User user);
}
