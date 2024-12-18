package ru.vladshi.springlearning.exceptions;

public class InvalidUserPasswordException extends RuntimeException {
    public InvalidUserPasswordException(String message) {
        super(message);
    }
}
