package ru.yandex.practicum.filmorate.exception;

public class UnknownObjectException extends RuntimeException{
    public UnknownObjectException(String message) {
        super(message);
    }
}
