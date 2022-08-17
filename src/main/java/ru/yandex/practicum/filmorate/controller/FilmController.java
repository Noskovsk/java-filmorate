package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.rmi.activation.UnknownObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();
    private long counter = 0;

    @GetMapping
    public List<Film> listFilms() {
        return new ArrayList<Film>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        additionalFilmValidation(film);
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Фильм :{}, успешно добавлен в библиотеку", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws UnknownObjectException {
        if(films.containsKey(film.getId())) {
            this.additionalFilmValidation(film);
            films.put(film.getId(),film);
            log.info("Фильм :{}, успешно обновлен в библиотеке", film);
            return film;
        } else {
            log.warn("Фильм :{}, не найден в библиотеке!", film);
            throw new UnknownObjectException("Такой фильм не найден!");
        }
    }

    private void additionalFilmValidation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза у фильма: {} должна быть больше 28 декабря 1895 года.", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть больше 28 декабря 1895 года.");
        }
    }
}
