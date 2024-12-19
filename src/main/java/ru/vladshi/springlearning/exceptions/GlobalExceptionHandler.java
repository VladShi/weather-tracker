package ru.vladshi.springlearning.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.ModelAndView;
import ru.vladshi.springlearning.dto.UserDto;

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
        return createModelAndViewWithErrorMessageAndUserDto(REGISTER_VIEW, LOGIN_ERROR_ATTRIBUTE, ex.getMessage());
    }

    @ExceptionHandler(UserLoginNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView handleUserLoginNotFoundException(UserLoginNotFoundException ex) {
        return createModelAndViewWithErrorMessageAndUserDto(LOGIN_VIEW, LOGIN_ERROR_ATTRIBUTE, ex.getMessage());
    }

    @ExceptionHandler(InvalidUserPasswordException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView handleInvalidUserPasswordException(InvalidUserPasswordException ex) {
        return createModelAndViewWithErrorMessageAndUserDto(LOGIN_VIEW, PASSWORD_ERROR_ATTRIBUTE, ex.getMessage());
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

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex) {
        // логирование и отправка оповещения кому-надо
        ex.printStackTrace();
        return ERROR_500_VIEW;
    }

    private ModelAndView createModelAndViewWithErrorMessageAndUserDto(String viewName,
                                                                      String errorAttribute,
                                                                      String errorMessage) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.addObject(errorAttribute, errorMessage);
        modelAndView.addObject(USER_ATTRIBUTE, new UserDto());
        return modelAndView;
    }
}
