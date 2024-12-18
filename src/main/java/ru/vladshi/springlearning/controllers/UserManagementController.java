package ru.vladshi.springlearning.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vladshi.springlearning.dto.UserDto;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;
import ru.vladshi.springlearning.exceptions.UserIsAlreadyAuthenticatedException;
import ru.vladshi.springlearning.exceptions.AuthenticationFailedException;
import ru.vladshi.springlearning.mappers.DtoMapper;
import ru.vladshi.springlearning.services.UserManagementService;
import ru.vladshi.springlearning.services.UserSessionsService;
import ru.vladshi.springlearning.Validators.UserValidator;

import java.util.Map;

import static ru.vladshi.springlearning.constants.ModelAttributeConstants.*;
import static ru.vladshi.springlearning.constants.RouteConstants.*;
import static ru.vladshi.springlearning.constants.ViewConstants.*;

@Controller
public class UserManagementController extends BaseController {

    private final UserSessionsService userSessionsService;
    private final UserManagementService userManagementService;
    private final int sessionCookieMaxAge;

    @Autowired
    public UserManagementController(UserSessionsService userSessionsService,
                                    UserManagementService userManagementService,
                                    @Value("${session.cookie.max-age-minutes:30}") int sessionCookieMaxAge) {
        this.userSessionsService = userSessionsService;
        this.userManagementService = userManagementService;
        this.sessionCookieMaxAge = sessionCookieMaxAge * 60;  // in seconds
    }

    @GetMapping(REGISTER_ROUTE)
    public String showRegisterForm(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                   @ModelAttribute(USER_ATTRIBUTE) UserDto userDto) {

        checkUserIsNotAuthenticated(sessionId);
        return REGISTER_VIEW;
    }

    @PostMapping(REGISTER_ROUTE)
    public String registerUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                               @ModelAttribute(USER_ATTRIBUTE) UserDto userDto,
                               Model model,
                               HttpServletResponse response) {

        checkUserIsNotAuthenticated(sessionId);

        Map<String, String> validationErrors = UserValidator.onRegister(userDto);
        if (!validationErrors.isEmpty()) {
            model.addAllAttributes(validationErrors);
            return REGISTER_VIEW;
        }

        User user = DtoMapper.toEntity(userDto);
        userManagementService.register(user);
        setSessionCookie(user, response);

        return REDIRECT_INDEX_PAGE;
    }

    @GetMapping(LOGIN_ROUTE)
    public String showLoginForm(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                @ModelAttribute(USER_ATTRIBUTE) UserDto userDto) {

        checkUserIsNotAuthenticated(sessionId);
        return LOGIN_VIEW;
    }

    @PostMapping(LOGIN_ROUTE)
    public String loginUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                            @ModelAttribute(USER_ATTRIBUTE) UserDto userDto,
                            Model model,
                            HttpServletResponse response) {

        checkUserIsNotAuthenticated(sessionId);

        Map<String, String> validationErrors = UserValidator.onLogIn(userDto);
        if (!validationErrors.isEmpty()) {
            model.addAllAttributes(validationErrors);
            return LOGIN_VIEW;
        }

        User user = DtoMapper.toEntity(userDto);
        userManagementService.logIn(user);
        setSessionCookie(user, response);

        return REDIRECT_INDEX_PAGE;
    }

    @GetMapping(LOGOUT_ROUTE)
    public String logoutUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                             HttpServletResponse response) {

        checkUserIsAuthenticated(sessionId);
        clearSessionCookie(response);

        return REDIRECT_INDEX_PAGE;
    }

    private void setSessionCookie(User user, HttpServletResponse response) {

        UserSession userSession = userSessionsService.findOrCreate(user);

        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, userSession.getId());
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(sessionCookieMaxAge);
        sessionCookie.setHttpOnly(true);

        response.addCookie(sessionCookie);
    }

    private void clearSessionCookie(HttpServletResponse response) {
        Cookie emptySessionCookie = new Cookie(SESSION_COOKIE_NAME, null);
        emptySessionCookie.setPath("/");
        emptySessionCookie.setMaxAge(0);
        emptySessionCookie.setHttpOnly(true);

        response.addCookie(emptySessionCookie);
    }

    private void checkUserIsNotAuthenticated(String sessionId) {
        if (userSessionsService.getById(sessionId).isPresent()) {
            throw new UserIsAlreadyAuthenticatedException("Access is denied to authenticated users.");
        }
    }

    private void checkUserIsAuthenticated(String sessionId) {
        if (userSessionsService.getById(sessionId).isEmpty()) {
            throw new AuthenticationFailedException("Access is denied to non-authenticated users.");
        }
    }
}
