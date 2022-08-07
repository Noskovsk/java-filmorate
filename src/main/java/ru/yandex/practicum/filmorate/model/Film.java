package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.ValidationException;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;

@Data
@AllArgsConstructor
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
    private LocalDate releaseDate;
    @Positive
    private Integer duration;

    public void additionalValidation() {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть большще 28 декабря 1895 года.");
        }
    }
}
