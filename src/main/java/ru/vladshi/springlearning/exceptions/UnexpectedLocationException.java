package ru.vladshi.springlearning.exceptions;

public class UnexpectedLocationException extends RuntimeException {
    public UnexpectedLocationException(String message) {
        super(message);
    }
}
