package ru.vladshi.springlearning.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vladshi.springlearning.entities.UserSession;
import ru.vladshi.springlearning.services.UserSessionsService;

import java.util.Optional;

import static ru.vladshi.springlearning.constants.RouteConstants.*;
import static ru.vladshi.springlearning.constants.ViewConstants.*;

@Controller
@RequestMapping(INDEX_PAGE_ROUTE)
public class IndexPageController extends BaseController {

    private final UserSessionsService userSessionsService;

    @Autowired
    public IndexPageController(UserSessionsService userSessionsService) {
        this.userSessionsService = userSessionsService;
    }

    @GetMapping()
    public String index(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId, Model model) {
        Optional<UserSession> userSessionOptional = userSessionsService.getUserSession(sessionId);
        model.addAttribute("userSessionOptional", userSessionOptional);
        // показать инфо пользователя
        return INDEX_PAGE_VIEW;
    }
}