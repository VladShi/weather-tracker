package ru.vladshi.springlearning.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.services.UserManagementService;

@Component
public class UserValidator implements Validator {

    private final UserManagementService userManagementService;

    @Autowired
    public UserValidator(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (userManagementService.isLoginTaken(user.getLogin())) {
            errors.rejectValue("login", "loginTaken", "Login is already taken");
        }
    }
}
