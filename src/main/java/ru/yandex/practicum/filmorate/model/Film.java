package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Slf4j
public class Film {
    private long id;
    @NonNull
    @NotBlank
    @NotEmpty
    private String name;
    @NonNull
    @Size(max = 200, message = "Длина описание не может быть больше 200 символов")
    private String description;
    @NotNull
    @NonNull
    private LocalDate releaseDate;
    @Positive
    @NonNull
    private Integer duration;

    public void additionalValidation() {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза у фильма: {} должна быть больше 28 декабря 1895 года.", this);
            throw new ValidationException("Дата релиза должна быть больше 28 декабря 1895 года.");
        }
    }
}
