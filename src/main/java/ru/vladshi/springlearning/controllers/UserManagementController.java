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
import ru.vladshi.springlearning.exceptions.UserAlreadyExistsException;
import ru.vladshi.springlearning.services.UserManagementService;
import ru.vladshi.springlearning.services.UserSessionsService;

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

    @GetMapping("/register")
    public String showRegisterForm(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                   @ModelAttribute("user") User user) {
        return isUserAuthenticated(sessionId) ? "redirect:/" : "register";
    }

    @PostMapping("/register")
    public String registerUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                               @ModelAttribute("user") @Valid User user,
                               BindingResult bindingResult,
                               HttpServletResponse response) {

        if (isUserAuthenticated(sessionId)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "register";
            // TODO кажется вместо bindingResult чище будет сделать свой валидатор выкидывающий кастомные исключения и
            //  глобальный обработчик ошибок для них. Тогда избавимся от if и try-catch в контроллере. Только не забыть
            //  в entity убрать аннотации и  поправить шаблоны представления. @ErrorHandler одинаковый для нескольких
            //  исключений возможен интересно?
        }

        try {
            sessionId = userManagementService.register(user);
        } catch (UserAlreadyExistsException ex) {
            bindingResult.rejectValue("login", "error.login", ex.getMessage());
            return "register";
        }

        setSessionCookie(response, sessionId);

        return "redirect:/";
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

        setSessionCookie(response, sessionId);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                         HttpServletResponse response) {

        if (!isUserAuthenticated(sessionId)) {
            return "redirect:/";
        }

        clearSessionCookie(response);

        return "redirect:/";
    }

    private boolean isUserAuthenticated(String sessionId) {
        return userSessionsService.getUserSession(sessionId).isPresent();
    }

    private void setSessionCookie(HttpServletResponse response, String sessionId) {
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
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
}
