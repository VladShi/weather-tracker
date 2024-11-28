package ru.vladshi.springlearning.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vladshi.springlearning.annotations.AuthenticationMode;
import ru.vladshi.springlearning.entities.User;
import ru.vladshi.springlearning.services.UserManagementService;

import static ru.vladshi.springlearning.annotations.AuthenticationMode.Mode.*;
import static ru.vladshi.springlearning.constants.ModelAttributeConstants.USER_ATTRIBUTE;
import static ru.vladshi.springlearning.constants.RouteConstants.*;
import static ru.vladshi.springlearning.constants.ViewConstants.*;

@Controller
@RequestMapping(INDEX_PAGE_ROUTE)
public class IndexPageController extends BaseController {

    private final UserManagementService userManagementService;

    @Autowired
    public IndexPageController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping()
    @AuthenticationMode(AUTHENTICATED)
    public String index(@CookieValue(value = SESSION_COOKIE_NAME, required = false) String sessionId,
                        Model model) {

        User user = userManagementService.getUserBySessionId(sessionId);
        model.addAttribute(USER_ATTRIBUTE, user);

        return INDEX_PAGE_VIEW;
    }
}