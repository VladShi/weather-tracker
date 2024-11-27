package ru.vladshi.springlearning.exceptions;

public class UserValidationOnRegisterException extends RuntimeException {
    public UserValidationOnRegisterException(String message) {
        super(message);
    }
}
