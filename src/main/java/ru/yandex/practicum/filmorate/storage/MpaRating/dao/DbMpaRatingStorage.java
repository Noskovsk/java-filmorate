package ru.yandex.practicum.filmorate.storage.MpaRating.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.MpaRatingMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRating.MpaRatingStorage;

import java.util.List;

@Slf4j
@Component("DbMpaRatingStorage")
@RequiredArgsConstructor
public class DbMpaRatingStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getAllMpaRatings() {
        String ratingsRows = "SELECT * FROM \"Rating_MPA\"";
        return jdbcTemplate.query(ratingsRows, new MpaRatingMapper());
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
            log.error("Рейтингов c таким id:{} не найдено!", ratingId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Рейтингов c таким id не найдено!");
        }
    }
}
