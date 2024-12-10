package ru.vladshi.springlearning.exceptions;

public class OpenWeatherException extends RuntimeException {
    public OpenWeatherException(String message) {
        super(message);
    }
}
