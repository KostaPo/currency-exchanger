package ru.kostapo.exceptions;

public class BadParameterException extends RuntimeException {
    public BadParameterException(String message) {
        super(message);
    }
}
