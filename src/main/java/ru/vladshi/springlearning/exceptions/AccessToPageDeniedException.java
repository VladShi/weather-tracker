package ru.vladshi.springlearning.exceptions;

public class AccessToPageDeniedException extends RuntimeException {
    public AccessToPageDeniedException(String message) {
        super(message);
    }
}
