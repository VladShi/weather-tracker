package ru.vladshi.springlearning.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
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
        return createErrorMessageWithUserModelAndView(REGISTER_VIEW, ex.getMessage());
    }

    @ExceptionHandler(UserValidationOnRegisterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleUserValidationOnRegister(UserValidationOnRegisterException ex) {
        return createErrorMessageWithUserModelAndView(REGISTER_VIEW, ex.getMessage());
    }

    @ExceptionHandler(UserValidationOnLogInException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleUserValidationOnLogIn(UserValidationOnLogInException ex) {
        return createErrorMessageWithUserModelAndView(LOGIN_VIEW, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView handleInvalidCredentials(InvalidCredentialsException ex) {
        return createErrorMessageWithUserModelAndView(LOGIN_VIEW, ex.getMessage());
    }

    @ExceptionHandler(OpenWeatherUnauthorizedException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleOpenWeatherException(OpenWeatherUnauthorizedException ex) {
        // логирование и отправка оповещения кому-надо
        return ERROR_500_VIEW;
    }

    @ExceptionHandler(OpenWeatherException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleOpenWeatherException(OpenWeatherException ex) {
        // логирование и отправка оповещения кому-надо
        return ERROR_500_VIEW;
    }

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleOpenWeatherException(ResourceAccessException ex) {
        // логирование и отправка оповещения кому-надо
        return ERROR_500_VIEW;
    }

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String handleGeneralException(Exception ex) {
//        // логирование и отправка оповещения кому-надо
//        return ERROR_500_VIEW;
//    }

    private ModelAndView createErrorMessageWithUserModelAndView(String viewName, String errorMessage) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject(ERROR_MESSAGE_ATTRIBUTE, errorMessage);
        modelAndView.addObject(USER_ATTRIBUTE, new User());
        return modelAndView;
    }
}
