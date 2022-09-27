package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@ToString
public class Film {
    private long id;
    @NotEmpty
    private String name;
    @NonNull
    @Size(max = 200, message = "Длина описание не может быть больше 200 символов")
    private String description;
    private Set<Genre> genres;
    @NonNull
    private MpaRating mpa;
    @NonNull
    private LocalDate releaseDate;
    @Positive
    @NonNull
    private Integer duration;
    private Set<Long> userLikes;
}
