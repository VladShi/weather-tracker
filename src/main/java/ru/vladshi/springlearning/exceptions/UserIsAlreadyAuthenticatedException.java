package ru.vladshi.springlearning.exceptions;

public class UserIsAlreadyAuthenticatedException extends RuntimeException {
    public UserIsAlreadyAuthenticatedException(String message) {
        super(message);
    }
}
