package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Genre.dao.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRating.dao.DbMpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.DbFilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMapper implements RowMapper<Film> {
    private final DbMpaRatingStorage dbMpaRatingStorage;
    private final DbGenreStorage dbGenreStorage;
    private final DbFilmStorage dbFilmStorage;

    public FilmMapper(DbMpaRatingStorage dbMpaRatingStorage, DbGenreStorage dbGenreStorage, DbFilmStorage dbFilmStorage) {
        this.dbMpaRatingStorage = dbMpaRatingStorage;
        this.dbGenreStorage = dbGenreStorage;
        this.dbFilmStorage = dbFilmStorage;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Film(rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                dbGenreStorage.findGenresOfFilm(rs.getLong("film_id")),
                dbMpaRatingStorage.getMpaRatingById(rs.getInt("rating_id")),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                dbFilmStorage.collectLikes(rs.getLong("film_id")));
    }
}
