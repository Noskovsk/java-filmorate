package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.dao.DbGenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final DbGenreStorage dbGenreStorage;

    @Autowired
    public GenreService(@Qualifier("DbGenreStorage") DbGenreStorage dbGenreStorage) {
        this.dbGenreStorage = dbGenreStorage;
    }

    public List<Genre> getAllGenre() {
        return dbGenreStorage.getAllGenre();
    }

    public Genre getGenreById(int genreId) {
        return dbGenreStorage.getGenreById(genreId);
    }
}
