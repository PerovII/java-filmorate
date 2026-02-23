package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        log.warn("Ошибка валидации запроса: " + errors);
        return errors;
    }

    @ExceptionHandler(EmptyIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleEmptyId(EmptyIdException exception) {
        log.warn("Ошибка валидации запроса: " + exception.getMessage());
        return Map.of("error", exception.getMessage());
    }

    @ExceptionHandler(EmptyDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(EmptyDataException exception) {
        log.warn("Ошибка: " + exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}