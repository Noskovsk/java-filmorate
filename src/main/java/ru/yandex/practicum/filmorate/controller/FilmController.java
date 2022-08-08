package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();
    private long counter = 0;

    @GetMapping("/films")
    public List<Film> listFilms() {
        return new ArrayList<Film>(films.values());
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        film.additionalValidation();
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Фильм :{}, успешно добавлен в библиотеку", film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws UnknownObjectException {
        if(films.containsKey(film.getId())) {
            film.additionalValidation();
            films.put(film.getId(),film);
            log.info("Фильм :{}, успешно обновлен в библиотеке", film);
            return film;
        } else {
            log.warn("Фильм :{}, не найден в библиотеке!", film);
            throw new UnknownObjectException("Такой фильм не найден!");
        }
    }
}
