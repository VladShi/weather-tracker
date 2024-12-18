package ru.vladshi.springlearning.Validators;

import ru.vladshi.springlearning.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

import static ru.vladshi.springlearning.constants.ModelAttributeConstants.LOGIN_ERROR_ATTRIBUTE;
import static ru.vladshi.springlearning.constants.ModelAttributeConstants.PASSWORD_ERROR_ATTRIBUTE;

public final class UserValidator {

    private UserValidator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Map<String, String> onRegister(UserDto userDto) {
        return validate(userDto, true);
    }

    public static Map<String, String> onLogIn(UserDto userDto) {
        return validate(userDto, false);
    }

    private static Map<String, String> validate(UserDto userDto, boolean checkPasswordMatches) {

        HashMap<String, String> validationErrors = new HashMap<>();

        String loginError = checkLogin(userDto.getLogin());
        if (!loginError.isEmpty()) {
            validationErrors.put(LOGIN_ERROR_ATTRIBUTE, loginError);
        }

        String passwordError = checkPassword(userDto.getPassword(), userDto.getConfirmPassword(), checkPasswordMatches);
        if (!passwordError.isEmpty()) {
            validationErrors.put(PASSWORD_ERROR_ATTRIBUTE, passwordError);
        }

        return validationErrors;
    }

    private static String checkLogin(String login) {
        if (isEmpty(login)) {
            return "username is required";
        }
        if (isLoginLengthInvalid(login)) {
            return "username length must be between 4 and 24 characters";
        }
        if (isLoginFormatInvalid(login)) {
            return "username may contain only english letters, digits and special characters from the set @.-_";
        }
        return "";
    }

    private static String checkPassword(String password, String confirmPassword, boolean checkPasswordMatches) {
        if (isEmpty(password)) {
            return "password is required";
        }
        if (isPasswordLengthInvalid(password)) {
            return "password length must be between 6 and 20 characters";
        }
        if (isPasswordFormatInvalid(password)) {
            return "password contains invalid characters. Password must contain " +
                        "at least one english letter, one digit, and one special character from the set @$!%*?&";
        }
        if (checkPasswordMatches && !confirmPassword.equals(password)) {
            return "password does not match repeated password";
        }
        return "";
    }

    private static boolean isPasswordFormatInvalid(String password) {
        return !password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$");
    }

    private static boolean isPasswordLengthInvalid(String password) {
        return password.length() < 6 || password.length() > 20;
    }

    private static boolean isLoginFormatInvalid(String login) {
        return !login.matches("^[a-zA-Z0-9@._-]+$");
    }

    private static boolean isLoginLengthInvalid(String login) {
        return login.length() < 4 || login.length() > 24;
    }

    private static boolean isEmpty(String string) {
        return string == null || string.isBlank();
    }
}
