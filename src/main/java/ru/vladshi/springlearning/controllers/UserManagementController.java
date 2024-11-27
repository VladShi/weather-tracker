package ru.vladshi.springlearning.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.entities.UserSession;
import ru.vladshi.springlearning.exceptions.AccessToPageDeniedException;
import ru.vladshi.springlearning.services.UserManagementService;
import ru.vladshi.springlearning.services.UserSessionsService;
import ru.vladshi.springlearning.Validators.UserValidator;

import static ru.vladshi.springlearning.constants.ModelAttributeConstants.*;
import static ru.vladshi.springlearning.constants.RouteConstants.*;
import static ru.vladshi.springlearning.constants.ViewConstants.*;

@Controller
public class UserManagementController extends BaseController {

    private final UserSessionsService userSessionsService;
    private final UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserSessionsService userSessionsService,
                                    UserManagementService userManagementService) {
        this.userSessionsService = userSessionsService;
        this.userManagementService = userManagementService;
    }

    @GetMapping(REGISTER_ROUTE)
    public String showRegisterForm(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                   @ModelAttribute(USER_ATTRIBUTE) User user) {

        checkUserIsNotAuthenticated(sessionId);
        return REGISTER_VIEW;
    }

    @PostMapping(REGISTER_ROUTE)
    public String registerUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                               @ModelAttribute(USER_ATTRIBUTE) User user,
                               HttpServletResponse response) {

        checkUserIsNotAuthenticated(sessionId);
        UserValidator.validateOnRegister(user);
        userManagementService.register(user);
        setSessionCookie(user, response);

        return REDIRECT_INDEX_PAGE;
    }

    @GetMapping(LOGIN_ROUTE)
    public String showLoginForm(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                @ModelAttribute(USER_ATTRIBUTE) User user) {

        checkUserIsNotAuthenticated(sessionId);
        return LOGIN_VIEW;
    }

    @PostMapping(LOGIN_ROUTE)
    public String loginUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                            @ModelAttribute(USER_ATTRIBUTE) User user,
                            HttpServletResponse response) {

        checkUserIsNotAuthenticated(sessionId);
        UserValidator.validateOnLogIn(user);
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
        sessionCookie.setMaxAge(60 * 60 * 24);  // TODO константу сколько время хранится куки на стороне юзера
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
        if (userSessionsService.getUserSession(sessionId).isPresent()) {
            throw new AccessToPageDeniedException("Access is denied to authenticated users.");
        }
    }

    private void checkUserIsAuthenticated(String sessionId) {
        if (userSessionsService.getUserSession(sessionId).isEmpty()) {
            throw new AccessToPageDeniedException("Access is denied to non-authenticated users.");
        }
    }
}
