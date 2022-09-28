package ru.yandex.practicum.filmorate.storage.MpaRating;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingStorage {
    List<MpaRating> getAllMpaRatings();

    MpaRating getMpaRatingById(int ratingId);
}
