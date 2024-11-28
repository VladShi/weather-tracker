package ru.vladshi.springlearning.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vladshi.springlearning.annotations.AuthenticationMode;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;
import ru.vladshi.springlearning.services.UserManagementService;
import ru.vladshi.springlearning.services.UserSessionsService;
import ru.vladshi.springlearning.Validators.UserValidator;

import static ru.vladshi.springlearning.annotations.AuthenticationMode.Mode.*;
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
    @AuthenticationMode(NOT_AUTHENTICATED)
    public String showRegisterForm(@ModelAttribute(USER_ATTRIBUTE) User user) {
        return REGISTER_VIEW;
    }

    @PostMapping(REGISTER_ROUTE)
    @AuthenticationMode(NOT_AUTHENTICATED)
    public String registerUser(@ModelAttribute(USER_ATTRIBUTE) User user, HttpServletResponse response) {

        UserValidator.validateOnRegister(user);
        userManagementService.register(user);
        setSessionCookie(user, response);

        return REDIRECT_INDEX_PAGE;
    }

    @GetMapping(LOGIN_ROUTE)
    @AuthenticationMode(NOT_AUTHENTICATED)
    public String showLoginForm(@ModelAttribute(USER_ATTRIBUTE) User user) {
        return LOGIN_VIEW;
    }

    @PostMapping(LOGIN_ROUTE)
    @AuthenticationMode(NOT_AUTHENTICATED)
    public String loginUser(@ModelAttribute(USER_ATTRIBUTE) User user,HttpServletResponse response) {

        UserValidator.validateOnLogIn(user);
        userManagementService.logIn(user);
        setSessionCookie(user, response);

        return REDIRECT_INDEX_PAGE;
    }

    @GetMapping(LOGOUT_ROUTE)
    @AuthenticationMode(AUTHENTICATED)
    public String logoutUser(HttpServletResponse response) {
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
}
