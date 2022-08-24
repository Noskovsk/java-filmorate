package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLikeToFilm(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        userStorage.findUserById(userId);
        film.getUserLikes().add(userId);
        log.info("Фильм с id: {} получил лайк от пользователя id: {}.", filmId, userId);
    }

    public void removeLikeFromFilm(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        userStorage.findUserById(userId);
        film.getUserLikes().remove(userId);
        log.info("Фильм с id: {} больше не нравится пользователю с id: {}.", filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage
                .listFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getUserLikes().size()).reversed())
                .collect(Collectors.toList())
                .subList(0, (count > filmStorage.listFilms().size()) ? filmStorage.listFilms().size() : count);
    }
}
