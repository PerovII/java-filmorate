package ru.yandex.practicum.filmorate.exception;

public class EmptyIdException extends RuntimeException {
    public EmptyIdException(String message) {
        super(message);
    }
}
