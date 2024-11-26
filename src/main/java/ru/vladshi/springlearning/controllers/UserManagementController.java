package ru.vladshi.springlearning.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.services.UserManagementService;
import ru.vladshi.springlearning.services.UserSessionsService;
import ru.vladshi.springlearning.validators.UserValidator;

@Controller
public class UserManagementController extends BaseController {

    private final UserValidator userValidator;
    private final UserSessionsService userSessionsService;
    private final UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserValidator userValidator,
                                    UserSessionsService userSessionsService,
                                    UserManagementService userManagementService) {
        this.userValidator = userValidator;
        this.userSessionsService = userSessionsService;
        this.userManagementService = userManagementService;
    }

    @GetMapping("/register")
    public String showRegisterForm(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                   @ModelAttribute("user") User user) {
        return isUserAuthenticated(sessionId) ? "redirect:/" : "register";
    }

    @PostMapping("/register")
    public String registerUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                               @ModelAttribute("user") @Valid User user,
                               BindingResult bindingResult) {

        if (isUserAuthenticated(sessionId)) {
            return "redirect:/";
        }

        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "register";
        }

        userManagementService.register(user);

        return "redirect:/login"; // TODO добавить автоматический логин сразу после регистрации
    }

    @GetMapping("/login")
    public String showLoginForm(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                @ModelAttribute("user") User user) {
        return isUserAuthenticated(sessionId) ? "redirect:/" : "login";
    }

    @PostMapping("/login")
    public String loginUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                            @ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                            HttpServletResponse response) {

        if (isUserAuthenticated(sessionId)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "login";
        }

        sessionId = userManagementService.logIn(user);
        if (sessionId.isEmpty()) {
            bindingResult.rejectValue("login", "error.login", "Incorrect login or password");
            return "login";
        }

        Cookie sessionIdCookie = buildSessionCookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(sessionIdCookie);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                         HttpServletResponse response) {

        if (!isUserAuthenticated(sessionId)) {
            return "redirect:/";
        }

        Cookie clearedSessionCookie = clearSessionCookie(SESSION_COOKIE_NAME);
        response.addCookie(clearedSessionCookie);

        return "redirect:/";
    }

    private boolean isUserAuthenticated(String sessionId) {
        return userSessionsService.getUserSession(sessionId).isPresent();
    }

    private Cookie buildSessionCookie(String cookieName, String value) {
        Cookie sessionIdCookie = new Cookie(cookieName, value);
        sessionIdCookie.setPath("/");
        sessionIdCookie.setMaxAge(60 * 60 * 24);  // TODO константу сколько время хранится куки на стороне юзера
        sessionIdCookie.setHttpOnly(true);
        return sessionIdCookie;
    }

    private Cookie clearSessionCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
