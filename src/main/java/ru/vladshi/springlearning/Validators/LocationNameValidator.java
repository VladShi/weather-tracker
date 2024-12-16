package ru.vladshi.springlearning.Validators;

public class LocationNameValidator {

    public static Boolean checkIsValid(String locationName) {
        return locationName != null && locationName.matches("^[a-zA-Zа-яА-ЯёЁ-]{2,40}$");
    }
}
