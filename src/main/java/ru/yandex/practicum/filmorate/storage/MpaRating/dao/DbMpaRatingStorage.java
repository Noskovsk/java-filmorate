package ru.yandex.practicum.filmorate.storage.MpaRating.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRating.MpaRatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("DbMPARatingStorage")
public class DbMpaRatingStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbMpaRatingStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> getAllMpaRatings() {
        String ratingsRows = "SELECT * FROM \"Rating_MPA\"";
        return jdbcTemplate.query(ratingsRows, (rs, rowNum) -> makeMpaRating(rs));
    }

    @Override
    public MpaRating getMpaRatingById(int ratingId) {
        SqlRowSet ratingsRow = jdbcTemplate.queryForRowSet("SELECT * FROM \"Rating_MPA\" WHERE \"rating_id\"=?", ratingId);
        if (ratingsRow.next()) {
            MpaRating mpaRating = new MpaRating(ratingsRow.getInt("rating_id"),
                    ratingsRow.getString("name"),
                    ratingsRow.getString("description"));
            log.info("Найден рейтинг c id: {}, название: {}", mpaRating.getId(), mpaRating.getName());
            return mpaRating;
        } else {
            log.warn("Рейтингов c таким id:{} не найдено!", ratingId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Рейтингов c таким id не найдено!");
        }
    }

    private MpaRating makeMpaRating(ResultSet ratingResultSet) throws SQLException {
        return new MpaRating(ratingResultSet.getInt("rating_id"),
                ratingResultSet.getString("name"),
                ratingResultSet.getString("description"));
    }
}
