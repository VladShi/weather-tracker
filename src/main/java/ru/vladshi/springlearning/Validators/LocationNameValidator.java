package ru.vladshi.springlearning.Validators;

public class LocationNameValidator {

    public static String validate(String locationName) {
        if (isEmpty(locationName)) {
            return "Location name is required";
        }
        if (isLengthInvalid(locationName)) {
            return "Location name must be between 2 and 35 characters";
        }
        if (isFormatInvalid(locationName)) {
            return "Location name may contain only english or russian letters and a dash '-' character";
        }
        return "";
    }

    private static boolean isFormatInvalid(String locationName) {
        return !locationName.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$");
    }

    private static boolean isLengthInvalid(String locationName) {
        return locationName.length() < 2 || locationName.length() > 35;
    }

    private static boolean isEmpty(String locationName) {
        return locationName == null || locationName.isBlank();
    }
}
