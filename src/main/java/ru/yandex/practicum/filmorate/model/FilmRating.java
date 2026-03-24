package ru.yandex.practicum.filmorate.model;

public enum FilmRating {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String value;

    FilmRating(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}