package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("DbFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> listFilms() {
        return filmStorage.listFilms();
    }

    public Film getFilmById(long filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void addLikeToFilm(long filmId, long userId) {
        filmStorage.addLikeToFilm(filmId, userId);
    }

    public void removeLikeFromFilm(long filmId, long userId) {
        filmStorage.removeLikeFromFilm(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }
}
