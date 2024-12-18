package ru.vladshi.springlearning.Validators;

import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.exceptions.UserValidationOnLogInException;
import ru.vladshi.springlearning.exceptions.UserValidationOnRegisterException;

public final class UserValidator {

    private static final String REGISTER = "register";
    private static final String LOGIN = "login";

    public UserValidator() {
    }

    public static void validateOnRegister(User user) {
        validate(user, REGISTER);
    }

    public static void validateOnLogIn(User user) {
        validate(user, LOGIN);
    }

    private static void validate(User user, String action) { // TODO изменить валидацию. Сделать не с помощью глобального перехватчика. Может быть возвращать какую-то, которую проверить потом в контроллере и передавать в модель
        ////// login validation //////
        String login = user.getLogin(); //

        if (login == null || !login.matches("^[a-zA-Z0-9-@._]{4,30}$")) {
            throwUserValidationExceptionByAction(action,"The username must be from 4 to 30 " +
                    "english letters and may contain digits, symbol '@', dots '.', underscores '_' and hyphens '-'.");
        }

        ////// password validation //////
        String password = user.getPassword();

        if (password == null || !password.matches("^[a-zA-Z0-9-!]{6,20}$")) {
            throwUserValidationExceptionByAction(action,"The password must be from 6 to 20 " +
                    "english letters and digits");
        }
    }

    private static void throwUserValidationExceptionByAction(String action, String message) {
        switch (action) {
            case REGISTER -> throw new UserValidationOnRegisterException(message);
            case LOGIN -> throw new UserValidationOnLogInException(message);
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }
    }
}
