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
import ru.vladshi.springlearning.services.UserManagementService;
import ru.vladshi.springlearning.services.UserSessionsService;
import ru.vladshi.springlearning.Validators.UserValidator;

@Controller
public class UserManagementController extends BaseController {

    private final UserSessionsService userSessionsService;
    private final UserManagementService userManagementService;
    private final UserValidator userValidator;

    @Autowired
    public UserManagementController(UserSessionsService userSessionsService,
                                    UserManagementService userManagementService,
                                    UserValidator userValidator) {
        this.userSessionsService = userSessionsService;
        this.userManagementService = userManagementService;
        this.userValidator = userValidator;
    }

    @GetMapping("/register")
    public String showRegisterForm(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                   @ModelAttribute("user") User user) {
        return isUserAuthenticated(sessionId) ? "redirect:/" : "register";
    }

    @PostMapping("/register")
    public String registerUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                               @ModelAttribute("user") User user,
                               HttpServletResponse response) {

        if (isUserAuthenticated(sessionId)) {
            // TODO сделать метод типа checkAuthentication() который будет выкидывать исключение, типа
            //  UserAlreadyAuth..(). И потом в глобальном обработчике отлавливать и перенаправлять.
            return "redirect:/";
        }
        userValidator.validateOnRegister(user);
        userManagementService.register(user);
        setSessionCookie(user, response);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String showLoginForm(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                                @ModelAttribute("user") User user) {
        return isUserAuthenticated(sessionId) ? "redirect:/" : "login";
    }

    @PostMapping("/login")
    public String loginUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                            @ModelAttribute("user") User user,
                            HttpServletResponse response) {

        if (isUserAuthenticated(sessionId)) {
            return "redirect:/";
        }
        userValidator.validateOnLogIn(user);
        userManagementService.logIn(user);
        setSessionCookie(user, response);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logoutUser(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
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
}
