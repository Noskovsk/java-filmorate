package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.rmi.activation.UnknownObjectException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ComponentScan
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long counter = 0;

    @Override
    public List<Film> listFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        additionalFilmValidation(film);
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Фильм :{}, успешно добавлен в библиотеку", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws UnknownObjectException {
        if(films.containsKey(film.getId())) {
            this.additionalFilmValidation(film);
            films.put(film.getId(),film);
            log.info("Фильм :{}, успешно обновлен в библиотеке", film);
            return film;
        } else {
            log.error("Фильм :{}, не найден в библиотеке!", film);
            throw new UnknownObjectException("Такой фильм не найден!");
        }
    }

    @Override
    public void additionalFilmValidation(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза у фильма: {} должна быть больше 28 декабря 1895 года.", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть больше 28 декабря 1895 года.");
        }
    }
}
