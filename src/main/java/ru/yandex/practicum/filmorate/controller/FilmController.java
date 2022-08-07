package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
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
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws UnknownObjectException {
        if(films.containsKey(film.getId())) {
            film.additionalValidation();
            films.put(film.getId(),film);
            return film;
        } else {
            throw new UnknownObjectException("Такой фильм не найден!");
        }
    }
}
