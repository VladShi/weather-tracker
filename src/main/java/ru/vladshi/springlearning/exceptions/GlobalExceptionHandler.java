package ru.vladshi.springlearning.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.vladshi.springlearning.entities.User;

import static ru.vladshi.springlearning.constants.RouteConstants.*;
import static ru.vladshi.springlearning.constants.ViewConstants.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessToPageDeniedException.class)
    public String handleAccessToPageDeniedException(AccessToPageDeniedException ex) {
        return REDIRECT_INDEX_PAGE;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ModelAndView handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return createModelAndViewForUser(REGISTER_VIEW, ex.getMessage());
    }

    @ExceptionHandler(UserValidationOnRegisterException.class)
    public ModelAndView handleUserValidationOnRegister(UserValidationOnRegisterException ex) {
        return createModelAndViewForUser(REGISTER_VIEW, ex.getMessage());
    }

    @ExceptionHandler(UserValidationOnLogInException.class)
    public ModelAndView handleUserValidationOnLogIn(UserValidationOnLogInException ex) {
        return createModelAndViewForUser(LOGIN_VIEW, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ModelAndView handleInvalidCredentials(InvalidCredentialsException ex) {
        return createModelAndViewForUser(LOGIN_VIEW, ex.getMessage());
    }

    private ModelAndView createModelAndViewForUser(String viewName, String errorMessage) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.addObject("user", new User());
        return modelAndView;
    }
}
