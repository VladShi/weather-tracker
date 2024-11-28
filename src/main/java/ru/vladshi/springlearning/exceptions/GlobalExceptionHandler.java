package ru.vladshi.springlearning.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import ru.vladshi.springlearning.entities.User;

import static ru.vladshi.springlearning.constants.ModelAttributeConstants.*;
import static ru.vladshi.springlearning.constants.RouteConstants.*;
import static ru.vladshi.springlearning.constants.ViewConstants.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserIsAlreadyAuthenticatedException.class)
    public String handleAccessToPageDeniedException() {
        return REDIRECT_INDEX_PAGE;
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public String handleAuthenticationFailedException() {
        return REDIRECT_LOGIN;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ModelAndView handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return createModelAndViewForUser(REGISTER_VIEW, ex.getMessage());
    }

    @ExceptionHandler(UserValidationOnRegisterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleUserValidationOnRegister(UserValidationOnRegisterException ex) {
        return createModelAndViewForUser(REGISTER_VIEW, ex.getMessage());
    }

    @ExceptionHandler(UserValidationOnLogInException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleUserValidationOnLogIn(UserValidationOnLogInException ex) {
        return createModelAndViewForUser(LOGIN_VIEW, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView handleInvalidCredentials(InvalidCredentialsException ex) {
        return createModelAndViewForUser(LOGIN_VIEW, ex.getMessage());
    }
// TODO добавить перехватчик неожиданных исключений
//  @ExceptionHandler(Exception.class)
//  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//  public String handleGeneralException(Exception ex) { ERROR_VIEW }

    private ModelAndView createModelAndViewForUser(String viewName, String errorMessage) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, errorMessage);
        modelAndView.addObject(USER_ATTRIBUTE, new User());
        return modelAndView;
    }
}
