package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> listFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film findFilmById(long filmId);

    void additionalFilmValidation(Film film);

    List<Film> getTopFilms(Integer count);

    void addLikeToFilm(long filmId, long userId);

    void removeLikeFromFilm(long filmId, long userId);
}
