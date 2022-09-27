package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ComponentScan
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final InMemoryUserStorage inMemoryUserStorage;
    private long counter = 0;

    public InMemoryFilmStorage(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public List<Film> listFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        additionalFilmValidation(film);
        film.setId(++counter);
        film.setUserLikes(new HashSet<>());
        films.put(film.getId(), film);
        log.info("Фильм :{}, успешно добавлен в библиотеку", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            additionalFilmValidation(film);
            films.put(film.getId(), film);
            log.info("Фильм :{}, успешно обновлен в библиотеке", film);
            return film;
        } else {
            log.error("Фильм :{}, не найден в библиотеке!", film);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такой фильм не найден!");
        }
    }

    @Override
    public Film findFilmById(long filmId) {
        if (films.containsKey(filmId)) {
            log.info("Фильм с filmId: {}, успешно найден в библиотеке", filmId);
            return films.get(filmId);
        } else {
            log.error("Фильм с filmId: {}, не найден в библиотеке!", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такой фильм не найден!");
        }
    }

    @Override
    public void additionalFilmValidation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза у фильма: {} должна быть больше 28 декабря 1895 года.", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть больше 28 декабря 1895 года.");
        }
        if (film.getUserLikes() == null) {
            film.setUserLikes(new HashSet<>());
        }
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return this
                .listFilms()
                .stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getUserLikes().size()).reversed())
                .collect(Collectors.toList())
                .subList(0, (count > this.listFilms().size()) ? this.listFilms().size() : count);
    }

    @Override
    public void addLikeToFilm(long filmId, long userId) {
        Film film = this.findFilmById(filmId);
        inMemoryUserStorage.findUserById(userId);
        film.getUserLikes().add(userId);
        log.info("Фильм с id: {} получил лайк от пользователя id: {}.", filmId, userId);
    }

    @Override
    public void removeLikeFromFilm(long filmId, long userId) {
        Film film = findFilmById(filmId);
        inMemoryUserStorage.findUserById(userId);
        film.getUserLikes().remove(userId);
        log.info("Фильм с id: {} больше не нравится пользователю с id: {}.", filmId, userId);
    }
}
