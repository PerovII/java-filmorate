package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotNull(message = "ID не может быть пустым")
    private Long id;

    @Email(message = "Некорректный формат email")
    private String email;

    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public boolean hasEmail() { return email != null && !email.isBlank(); }
    public boolean hasLogin() { return login != null && !login.isBlank(); }
    public boolean hasName() { return name != null && !name.isBlank(); }
    public boolean hasBirthday() { return birthday != null; }
}