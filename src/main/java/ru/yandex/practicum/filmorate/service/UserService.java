package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriendById(long userId, long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        user.getFriendSet().add(friendId);
        friend.getFriendSet().add(userId);
        log.info("Пользователи с id: {} и {} теперь друзья.", userId, friendId);
    }

    public void removeFriendById(long userId, long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        if (isUsersFriends(user, friend)) {
            user.getFriendSet().remove(friendId);
            friend.getFriendSet().remove(userId);
            log.info("Пользователи с id: {} и {} больше не являются друзьями.", userId, friendId);
        } else {
            log.warn("Пользователь с userId: {} не найден!", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователи не являются друзьями!");
        }
    }

    public boolean isUsersFriends(User user, User friend) {
        return user.getFriendSet().contains(friend.getId()) && friend.getFriendSet().contains(user.getId());
    }

    public List<User> getUserFriends(long userId) {
        User user = userStorage.findUserById(userId);
        List<User> friendList = new ArrayList<>();
        for (Long friendId : user.getFriendSet()) {
            friendList.add(userStorage.findUserById(friendId));
        }
        //TODO попробовать через stream
        return friendList;
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        List<User> friendList = new ArrayList<>();
        List<Long> friendIdList;
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherUserId);
        friendIdList = user.getFriendSet().stream().filter(u -> otherUser.getFriendSet().contains(u)).collect(Collectors.toList());
        for (Long friendId : friendIdList) {
            friendList.add(userStorage.findUserById(friendId));
        }
        return friendList;
    }
}
