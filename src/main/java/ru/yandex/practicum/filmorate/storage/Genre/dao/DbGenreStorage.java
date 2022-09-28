package ru.yandex.practicum.filmorate.storage.Genre.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenreStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component("DbGenreStorage")
@RequiredArgsConstructor
public class DbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenre() {
        String genresRows = "SELECT * FROM \"Genre\"";
        return jdbcTemplate.query(genresRows, new GenreMapper());
    }

    @Override
    public Genre getGenreById(int genreId) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM \"Genre\" WHERE \"genre_id\"=?", genreId);
        if (genreRows.next()) {
            Genre genre = new Genre(genreRows.getInt("genre_id"), genreRows.getString("name"));
            log.info("Найден жанр c id: {}, название: {}", genre.getId(), genre.getName());
            return genre;
        } else {
            log.error("Жанров c таким id:{} не найдено!", genreId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Жанров c таким id не найдено!");
        }
    }

    public Set<Genre> findGenresOfFilm(long filmId) {
        String genresRows = "SELECT * FROM \"Genre\" " +
                "INNER JOIN \"Films_Genre\" FG " +
                "ON \"Genre\".\"genre_id\" = FG.\"genre_id\" " +
                "WHERE FG.\"film_id\"  = ? ";
        return new HashSet<>(jdbcTemplate.query(genresRows, new GenreMapper(), filmId));
    }
}
