package ru.vladshi.springlearning.Validators;

import org.springframework.stereotype.Component;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.exceptions.UserValidationOnLogInException;
import ru.vladshi.springlearning.exceptions.UserValidationOnRegisterException;

@Component
public class UserValidator {

    private static final String REGISTER = "register";
    private static final String LOGIN = "login";

    public UserValidator() {
    }

    public void validateOnRegister(User user) {
        validate(user, REGISTER);
    }

    public void validateOnLogIn(User user) {
        validate(user, LOGIN);
    }

    private void validate(User user, String action) {
        ////// login validation //////
        String login = user.getLogin();

        if (login == null || login.isEmpty()) {
            throwUserValidationExceptionByAction(action,"Login is required");
        }

        if (login.length() < 4 || login.length() > 30) {
            throwUserValidationExceptionByAction(action,"Login length must be between 4 and 30 characters");
        }

        ////// password validation //////
        String password = user.getPassword();

        if (password == null || password.isEmpty()) {
            throwUserValidationExceptionByAction(action,"Password is required");
        }

        if (password.length() < 6 || password.length() > 16) {
            throwUserValidationExceptionByAction(action,"Password length must be between 6 and 20 characters");
        }
    }

    private void throwUserValidationExceptionByAction(String action, String message) {
        switch (action) {
            case REGISTER -> throw new UserValidationOnRegisterException(message);
            case LOGIN -> throw new UserValidationOnLogInException(message);
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }
    }
}
