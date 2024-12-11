package ru.vladshi.springlearning.exceptions;

public class OpenWeatherUnauthorizedException extends RuntimeException {
    public OpenWeatherUnauthorizedException(String message) {
        super(message);
    }
}
