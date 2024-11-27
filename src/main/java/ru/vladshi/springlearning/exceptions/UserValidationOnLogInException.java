package ru.vladshi.springlearning.exceptions;

public class UserValidationOnLogInException extends RuntimeException {
    public UserValidationOnLogInException(String message) {
        super(message);
    }
}
