package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
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
            this.name = this.login;
        }
    }
}
