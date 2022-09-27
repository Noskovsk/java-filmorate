package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRating.dao.DbMpaRatingStorage;

import java.util.List;

@Service
public class MpaRatingService {
    private final DbMpaRatingStorage dbMpaRatingStorage;

    public MpaRatingService(DbMpaRatingStorage dbMpaRatingStorage) {
        this.dbMpaRatingStorage = dbMpaRatingStorage;
    }

    public List<MpaRating> getAllMpaRatings() {
        return dbMpaRatingStorage.getAllMpaRatings();
    }

    public MpaRating getMpaRatingById(int id) {
        return dbMpaRatingStorage.getMpaRatingById(id);
    }
}
