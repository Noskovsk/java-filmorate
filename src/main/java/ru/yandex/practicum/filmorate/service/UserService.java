package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
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
    public UserService(@Qualifier("DbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> listUsers() {
        return userStorage.listUsers();
    }

    public User getUserById(long userId) {
        return userStorage.findUserById(userId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriendById(long userId, long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriendById(long userId, long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends(long userId) {
        User user = userStorage.findUserById(userId);
        List<User> friendList = new ArrayList<>();
        for (Long friendId : userStorage.getFriendsId(user.getId())) {
            friendList.add(userStorage.findUserById(friendId));
        }
        return friendList;
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        List<User> friendList = new ArrayList<>();
        List<Long> friendIdList;
        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherUserId);
        if ((userStorage.getFriendsId(user.getId()) != null) && (userStorage.getFriendsId(otherUser.getId()) != null)) {
            friendIdList = userStorage.getFriendsId(user.getId()).stream().filter(u -> userStorage.getFriendsId(otherUser.getId()).contains(u)).collect(Collectors.toList());
            for (Long friendId : friendIdList) {
                friendList.add(userStorage.findUserById(friendId));
            }
        }
        return friendList;
    }
}
