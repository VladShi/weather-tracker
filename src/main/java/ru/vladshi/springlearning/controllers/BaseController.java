package ru.vladshi.springlearning.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.vladshi.springlearning.services.UserManagementService;

@Controller
public abstract class BaseController {

    protected final static String SESSION_COOKIE_NAME = "JSESSIONID";

    protected final UserManagementService userManagementService;

    @Autowired
    public BaseController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }
}
