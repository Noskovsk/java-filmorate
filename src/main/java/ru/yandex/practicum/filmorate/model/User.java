package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private long id;
    @NonNull
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "^\\S*$", message = "Ошибка. В Логине не должно содержаться пробелов!")
    private String login;
    private String name;
    @NonNull
    @Past
    private LocalDate birthday;
    private Set<Long> friendSet;
}
