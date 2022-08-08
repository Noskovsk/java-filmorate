package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Slf4j
public class User {
    private long id;
    @NonNull
    @Email
    private String email;
    @NonNull
    @NotEmpty
    @NotBlank
    @Pattern(regexp = "^\\S*$", message = "Ошибка. В Логине не должно содержаться пробелов!")
    private String login;
    private String name = "";
    @NonNull
    @Past
    private LocalDate birthday;

    public void normalaize() {
        if (name.isEmpty()) {
            log.info("У переданного пользователя: {}, поле name=пусто, устанавливаем равным login.", this);
            this.name = this.login;
        }
    }
}
