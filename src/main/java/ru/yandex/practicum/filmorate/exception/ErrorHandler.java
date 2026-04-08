package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(err ->
                errors.putIfAbsent(
                        err.getField(),
                        err.getDefaultMessage()
                )
        );

        log.warn("Ошибка валидации: {}", errors);

        return new ErrorResponse(errors);
    }

    @ExceptionHandler(EmptyIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmptyId(EmptyIdException exception) {

        log.warn("Ошибка: {}", exception.getMessage());

        return new ErrorResponse(
                Map.of("id", exception.getMessage())
        );
    }

    @ExceptionHandler(EmptyDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(EmptyDataException exception) {

        log.warn("Ошибка: {}", exception.getMessage());

        return new ErrorResponse(
                Map.of("error", exception.getMessage())
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException exception) {

        log.warn("Ошибка: {}", exception.getMessage());

        return new ErrorResponse(
                Map.of("error", exception.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception) {

        log.warn("Ошибка: {}", exception.getMessage());

        return new ErrorResponse(
                Map.of("error", exception.getMessage())
        );
    }
}