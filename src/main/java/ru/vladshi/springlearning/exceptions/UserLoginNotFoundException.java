package ru.vladshi.springlearning.exceptions;

public class UserLoginNotFoundException extends RuntimeException {
    public UserLoginNotFoundException(String message) {
        super(message);
    }
}
