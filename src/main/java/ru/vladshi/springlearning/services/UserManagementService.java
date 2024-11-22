package ru.vladshi.springlearning.services;

import jakarta.servlet.http.HttpServletResponse;
import ru.vladshi.springlearning.entities.User;


public interface UserManagementService {

    String logIn(User user);

    void logOut(String sessionIdName, HttpServletResponse response);

    void register(User user);

    boolean isLoginTaken(String login);
}
