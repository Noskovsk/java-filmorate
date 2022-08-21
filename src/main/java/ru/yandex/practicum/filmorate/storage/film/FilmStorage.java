package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.rmi.activation.UnknownObjectException;
import java.util.List;

public interface FilmStorage {

    List<Film> listFilms();

    Film addFilm(Film film) ;

    Film updateFilm(Film film) throws UnknownObjectException;

    void additionalFilmValidation(Film film);
}
