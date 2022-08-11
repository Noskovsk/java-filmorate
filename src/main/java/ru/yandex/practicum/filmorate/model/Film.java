package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Slf4j
public class Film {
    private long id;
    @NotEmpty
    private String name;
    @NonNull
    @Size(max = 200, message = "Длина описание не может быть больше 200 символов")
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @Positive
    @NonNull
    private Integer duration;
}
