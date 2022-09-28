package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.Genre.dao.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRating.dao.DbMpaRatingStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmoRateApplicationTests {

    private final DbGenreStorage genreStorage;
    private final DbMpaRatingStorage mpaRatingStorage;

    @Test
    public void testFindGenreById() {
        Genre genre = genreStorage.getGenreById(1);
        assertEquals("Комедия", genre.getName(), "Неверное значение наименования жанра. Проверьте запрос.");
    }

    @Test
    public void testFindMpaRatingById() {
        MpaRating mpaRating = mpaRatingStorage.getMpaRatingById(1);
        assertEquals("G", mpaRating.getName(), "Неверное значение наименования жанра. Проверьте запрос.");
    }
}
