package ru.yandex.practicum.filmorate.storage.film.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.dao.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRating.dao.DbMpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.DbUserStorage;

import javax.validation.ValidationException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("DbFilmStorage")
public class DbFilmStorage implements FilmStorage {
    private final DbMpaRatingStorage dbMpaRatingStorage;
    private final DbGenreStorage dbGenreStorage;
    private final DbUserStorage dbUserStorage;
    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorage(DbMpaRatingStorage dbMpaRatingStorage, DbGenreStorage dbGenreStorage, DbUserStorage dbUserStorage, JdbcTemplate jdbcTemplate) {
        this.dbMpaRatingStorage = dbMpaRatingStorage;
        this.dbGenreStorage = dbGenreStorage;
        this.dbUserStorage = dbUserStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> listFilms() {
        String filmsRows = "SELECT * FROM \"Film\"";
        return jdbcTemplate.query(filmsRows, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addFilm(Film film) {
        additionalFilmValidation(film);
        film.setUserLikes(new HashSet<>());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertSql = "INSERT INTO \"Film\" (\"name\", \"description\", \"rating_id\", \"release_date\", \"duration\") VALUES (?, ?, ?, ?, ?)";
        Integer rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getMpa().getId());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            ps.setInt(5, film.getDuration());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKeyAs(Long.class));
        log.info("Фильм :{}, успешно добавлен в библиотеку.  Всего добавлено строк: {}", film.getId(), rows);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                makeLinkFilmToGenre(film.getId(), genre.getId());
            }
        }
        return findFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        findFilmById(film.getId());
        this.additionalFilmValidation(film);
        String updateSql = "UPDATE \"Film\" " +
                "SET \"name\"=?, \"description\"=?, \"release_date\"=?, \"duration\"=?, \"rating_id\"=? " +
                "WHERE \"film_id\"=?";
        Integer rows = jdbcTemplate.update(updateSql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (rows == 1) {
            log.info("Данные фильма с id: {} успешно обновлены в каталоге. Обновлено строк: {}", film.getId(), rows);
            deleteAllLinksFromFilmToGenre(film.getId());
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                for (Genre genre : film.getGenres()) {
                    makeLinkFilmToGenre(film.getId(), genre.getId());
                }
            }
            return findFilmById(film.getId());
        } else {
            log.warn("Что-то пошло не так! Обновлено строк: {}", rows);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Что-то пошло не так!");
        }
    }

    @Override
    public Film findFilmById(long filmId) {
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet("SELECT * FROM \"Film\" WHERE \"film_id\" = ?", filmId);
        if (filmRow.next()) {
            Film film = makeFilm(filmRow);
            log.info("Найден фильм c id: {}, название: {}", film.getId(), film.getName());
            return film;
        } else {
            log.warn("Фильм с Id: {} не найден!", filmId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такой фильм не найден!");
        }
    }

    @Override
    public void additionalFilmValidation(Film film) {
        log.warn(film.toString());
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза у фильма: {} должна быть больше 28 декабря 1895 года.", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть больше 28 декабря 1895 года.");
        }
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        String filmsRows = "SELECT * FROM \"Film\" F " +
                "LEFT JOIN \"User_Likes\" UL on F.\"film_id\" = UL.\"film_id\" " +
                "GROUP BY  F.\"film_id\" " +
                "ORDER BY COUNT(UL.\"user_id\") DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(filmsRows, (rs, rowNum) -> makeFilm(rs), count);
    }

    public void addLikeToFilm(long filmId, long userId) {
        findFilmById(filmId);
        dbUserStorage.findUserById(userId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertSql = "INSERT INTO \"User_Likes\" (\"film_id\", \"user_id\") VALUES (?, ?)";
        Integer rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"film_id"});
            ps.setLong(1, filmId);
            ps.setLong(2, userId);
            return ps;
        }, keyHolder);
        log.info("К фильму :{}, успешно добавлен лайк от пользователя:{} .  Всего добавлено строк: {}", filmId, userId, rows);
    }

    public void removeLikeFromFilm(long filmId, long userId) {
        findFilmById(filmId);
        dbUserStorage.findUserById(userId);
        String deleteSql = "DELETE FROM \"User_Likes\" WHERE (\"film_id\"=? AND \"user_id\"=?)";
        Integer rows = jdbcTemplate.update(deleteSql, filmId, userId);
        log.info("Фильм с id: {} больше не нравится пользователю с id: {}. Удалено строк {}:", filmId, userId, rows);
    }

    private Film makeFilm(SqlRowSet sqlUserRow) {
        return new Film(sqlUserRow.getLong("film_id"),
                sqlUserRow.getString("name"),
                sqlUserRow.getString("description"),
                dbGenreStorage.findGenresOfFilm(sqlUserRow.getLong("film_id")),
                dbMpaRatingStorage.getMpaRatingById(sqlUserRow.getInt("rating_id")),
                sqlUserRow.getDate("release_date").toLocalDate(),
                sqlUserRow.getInt("duration"),
                collectLikes(sqlUserRow.getLong("film_id")));
    }

    private Film makeFilm(ResultSet sqlUserRow) throws SQLException {
        return new Film(sqlUserRow.getLong("film_id"),
                sqlUserRow.getString("name"),
                sqlUserRow.getString("description"),
                dbGenreStorage.findGenresOfFilm(sqlUserRow.getLong("film_id")),
                dbMpaRatingStorage.getMpaRatingById(sqlUserRow.getInt("rating_id")),
                sqlUserRow.getDate("release_date").toLocalDate(),
                sqlUserRow.getInt("duration"),
                collectLikes(sqlUserRow.getLong("film_id")));
    }

    private Set<Long> collectLikes(long filmId) {
        String filmsRows = "SELECT * FROM \"User_Likes\" WHERE \"film_id\" = ? ";
        return new HashSet<>(jdbcTemplate.query(filmsRows, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    private void makeLinkFilmToGenre(long filmId, long genreId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertSql = "INSERT INTO \"Films_Genre\" (\"film_id\", \"genre_id\") VALUES (?, ?)";
        Integer rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"film_id"});
            ps.setLong(1, filmId);
            ps.setLong(2, genreId);
            return ps;
        }, keyHolder);
        log.info("К фильму :{}, успешно добавлен в жанр:{}.  Всего добавлено строк: {}", filmId, genreId, rows);
    }

    private void deleteAllLinksFromFilmToGenre(long filmId) {
        String deleteSql = "DELETE FROM \"Films_Genre\" WHERE (\"film_id\"=?)";
        Integer rows = jdbcTemplate.update(deleteSql, filmId);
        log.info("Фильм с id: {}. Удалены все связи с жанрами. Удалено строк {}:", filmId, rows);
    }

}
